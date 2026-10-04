package com.example.auditoria.adapter.out.persistence;

import com.example.auditoria.domain.entity.HallazgoAuditoria;
import com.example.auditoria.domain.valueobject.EstadoHallazgo;
import com.example.auditoria.domain.valueobject.HallazgoId;
import com.example.auditoria.domain.valueobject.PlanRemediacion;
import com.example.auditoria.domain.valueobject.Severidad;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import(HallazgoRepositoryAdapter.class)
@DisplayName("Pruebas de persistencia del adaptador HallazgoRepositoryAdapter")
class HallazgoRepositoryAdapterTest {

    @Autowired
    private HallazgoRepositoryAdapter adapter;

    @Test
    @DisplayName("Persistencia ida y vuelta de un hallazgo CERRADO conserva fechaCierre histórica")
    void persistirYRecuperarHallazgoCerrado() {
        HallazgoId id = HallazgoId.nuevo();
        PlanRemediacion plan = new PlanRemediacion("Auditor Responsable", LocalDate.of(2026, 9, 15), "Corrección realizada");
        LocalDate fechaCierreHistorica = LocalDate.of(2026, 9, 20);

        HallazgoAuditoria hallazgoCerrado = HallazgoAuditoria.reconstituir(
                id,
                "Falla en control de inventario",
                "Diferencias físicas vs sistema",
                "Almacén",
                Severidad.ALTA,
                LocalDate.of(2026, 8, 1),
                EstadoHallazgo.CERRADO,
                plan,
                fechaCierreHistorica
        );

        adapter.guardar(hallazgoCerrado);

        Optional<HallazgoAuditoria> recuperado = adapter.buscarPorId(id);
        assertTrue(recuperado.isPresent());
        HallazgoAuditoria h = recuperado.get();
        assertEquals(EstadoHallazgo.CERRADO, h.getEstado());
        assertEquals(fechaCierreHistorica, h.getFechaCierre());
        assertNotNull(h.getPlanRemediacion());
        assertEquals("Auditor Responsable", h.getPlanRemediacion().responsable());
    }

    @Test
    @DisplayName("Persistencia ida y vuelta de un hallazgo REABIERTO no lanza TransicionInvalidaException")
    void persistirYRecuperarHallazgoReabierto() {
        HallazgoId id = HallazgoId.nuevo();
        PlanRemediacion plan = new PlanRemediacion("Auditor Responsable", LocalDate.of(2026, 9, 15), "Notas previas");

        HallazgoAuditoria hallazgoReabierto = HallazgoAuditoria.reconstituir(
                id,
                "Inconsistencia contable",
                "Partidas conciliatorias pendientes",
                "Contabilidad",
                Severidad.CRITICA,
                LocalDate.of(2026, 7, 15),
                EstadoHallazgo.REABIERTO,
                plan,
                null
        );

        adapter.guardar(hallazgoReabierto);

        Optional<HallazgoAuditoria> recuperado = adapter.buscarPorId(id);
        assertTrue(recuperado.isPresent());
        HallazgoAuditoria h = recuperado.get();
        assertEquals(EstadoHallazgo.REABIERTO, h.getEstado());
        assertNull(h.getFechaCierre());
        assertEquals("Contabilidad", h.getArea());
    }

    @Test
    @DisplayName("Proyecciones de dashboard agregan conteos por severidad, estado y promedio de días de cierre")
    void proyeccionesDashboard() {
        PlanRemediacion plan = new PlanRemediacion("Responsable", LocalDate.of(2026, 9, 1), "Notas");

        // 1. Abierto en Operaciones
        adapter.guardar(HallazgoAuditoria.reconstituir(
                HallazgoId.nuevo(), "H1", "D1", "Operaciones", Severidad.CRITICA,
                LocalDate.of(2026, 8, 1), EstadoHallazgo.ABIERTO, null, null
        ));

        // 2. Cerrado en Finanzas (10 días: 1 al 11)
        adapter.guardar(HallazgoAuditoria.reconstituir(
                HallazgoId.nuevo(), "H2", "D2", "Finanzas", Severidad.ALTA,
                LocalDate.of(2026, 8, 1), EstadoHallazgo.CERRADO, plan, LocalDate.of(2026, 8, 11)
        ));

        // 3. Cerrado en Finanzas (20 días: 1 al 21)
        adapter.guardar(HallazgoAuditoria.reconstituir(
                HallazgoId.nuevo(), "H3", "D3", "Finanzas", Severidad.MEDIA,
                LocalDate.of(2026, 8, 1), EstadoHallazgo.CERRADO, plan, LocalDate.of(2026, 8, 21)
        ));

        var porSeveridad = adapter.contarPorSeveridad();
        var porEstado = adapter.contarPorEstado();
        var promedios = adapter.promedioDiasCierrePorArea();

        assertFalse(porSeveridad.isEmpty());
        assertFalse(porEstado.isEmpty());
        assertFalse(promedios.isEmpty());

        var finanzasPromedio = promedios.stream()
                .filter(p -> p.area().equalsIgnoreCase("Finanzas"))
                .findFirst()
                .orElseThrow();

        assertEquals(15.0, finanzasPromedio.promedioDias(), 0.01);
    }
}
