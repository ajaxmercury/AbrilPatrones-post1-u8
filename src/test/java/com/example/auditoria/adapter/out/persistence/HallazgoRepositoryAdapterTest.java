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
}
