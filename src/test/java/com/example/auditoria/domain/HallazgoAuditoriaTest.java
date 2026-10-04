package com.example.auditoria.domain;

import com.example.auditoria.domain.entity.HallazgoAuditoria;
import com.example.auditoria.domain.valueobject.EstadoHallazgo;
import com.example.auditoria.domain.valueobject.HallazgoId;
import com.example.auditoria.domain.valueobject.PlanRemediacion;
import com.example.auditoria.domain.valueobject.Severidad;
import com.example.auditoria.domain.valueobject.TransicionInvalidaException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pruebas unitarias del Aggregate Root HallazgoAuditoria (sin Spring)")
class HallazgoAuditoriaTest {

    private HallazgoAuditoria crearHallazgoBase() {
        return new HallazgoAuditoria(
                HallazgoId.nuevo(),
                "Falta de segregación de funciones",
                "El rol de tesorería también aprueba pagos bancarios",
                "Finanzas",
                Severidad.ALTA,
                LocalDate.of(2026, 8, 10)
        );
    }

    private PlanRemediacion crearPlanValido() {
        return new PlanRemediacion(
                "Carlos Mendoza",
                LocalDate.of(2026, 9, 30),
                "Implementar doble factor de aprobación y separar roles en el ERP"
        );
    }

    @Test
    @DisplayName("Un hallazgo nuevo se inicializa en estado ABIERTO sin plan ni fecha de cierre")
    void inicializacionCorrecta() {
        HallazgoAuditoria h = crearHallazgoBase();

        assertEquals(EstadoHallazgo.ABIERTO, h.getEstado());
        assertNull(h.getPlanRemediacion());
        assertNull(h.getFechaCierre());
        assertEquals("Finanzas", h.getArea());
        assertEquals(Severidad.ALTA, h.getSeveridad());
    }

    @Test
    @DisplayName("Flujo de ciclo de vida completo válido: ABIERTO -> EN_REMEDIACION -> CERRADO -> REABIERTO -> EN_REMEDIACION")
    void flujoCicloDeVidaCompletoValido() {
        HallazgoAuditoria h = crearHallazgoBase();
        PlanRemediacion plan = crearPlanValido();

        // 1. Iniciar remediación
        EstadoHallazgo ant1 = h.iniciarRemediacion(plan);
        assertEquals(EstadoHallazgo.ABIERTO, ant1);
        assertEquals(EstadoHallazgo.EN_REMEDIACION, h.getEstado());
        assertEquals(plan, h.getPlanRemediacion());
        assertNull(h.getFechaCierre());

        // 2. Cerrar hallazgo
        EstadoHallazgo ant2 = h.cerrar();
        assertEquals(EstadoHallazgo.EN_REMEDIACION, ant2);
        assertEquals(EstadoHallazgo.CERRADO, h.getEstado());
        assertNotNull(h.getFechaCierre());
        assertEquals(LocalDate.now(), h.getFechaCierre());

        // 3. Reabrir hallazgo
        EstadoHallazgo ant3 = h.reabrir();
        assertEquals(EstadoHallazgo.CERRADO, ant3);
        assertEquals(EstadoHallazgo.REABIERTO, h.getEstado());
        assertNull(h.getFechaCierre());

        // 4. Iniciar remediación nuevamente desde REABIERTO
        PlanRemediacion plan2 = new PlanRemediacion("Diana López", LocalDate.of(2026, 11, 15), "Revisión extendida");
        EstadoHallazgo ant4 = h.iniciarRemediacion(plan2);
        assertEquals(EstadoHallazgo.REABIERTO, ant4);
        assertEquals(EstadoHallazgo.EN_REMEDIACION, h.getEstado());
        assertEquals(plan2, h.getPlanRemediacion());
    }

    @Test
    @DisplayName("Cerrar un hallazgo en estado ABIERTO sin remediación lanza excepción")
    void cerrarSinRemediacionLanzaExcepcion() {
        HallazgoAuditoria h = crearHallazgoBase();
        assertThrows(IllegalStateException.class, h::cerrar);
    }

    @Test
    @DisplayName("Reabrir un hallazgo en estado ABIERTO lanza TransicionInvalidaException")
    void reabrirHallazgoAbiertoLanzaTransicionInvalidaException() {
        HallazgoAuditoria h = crearHallazgoBase();
        assertThrows(TransicionInvalidaException.class, h::reabrir);
    }

    @Test
    @DisplayName("Reabrir un hallazgo EN_REMEDIACION lanza TransicionInvalidaException")
    void reabrirHallazgoEnRemediacionLanzaTransicionInvalidaException() {
        HallazgoAuditoria h = crearHallazgoBase();
        h.iniciarRemediacion(crearPlanValido());
        assertThrows(TransicionInvalidaException.class, h::reabrir);
    }

    @Test
    @DisplayName("iniciarRemediacion valida plan nulo antes de transicionar (corrección obligatoria)")
    void iniciarRemediacionValidaPlanNuloAntesDeTransicionar() {
        HallazgoAuditoria h = crearHallazgoBase();
        assertThrows(NullPointerException.class, () -> h.iniciarRemediacion(null));
        assertEquals(EstadoHallazgo.ABIERTO, h.getEstado());
    }

    @Test
    @DisplayName("Constructor valida severidad y fecha de detección no nulas (corrección obligatoria)")
    void constructorValidaSeveridadYFecha() {
        HallazgoId id = HallazgoId.nuevo();
        assertThrows(NullPointerException.class, () -> new HallazgoAuditoria(
                id, "Titulo", "Desc", "Area", null, LocalDate.now()
        ));
        assertThrows(NullPointerException.class, () -> new HallazgoAuditoria(
                id, "Titulo", "Desc", "Area", Severidad.MEDIA, null
        ));
    }

    @Test
    @DisplayName("reconstituir restaura un hallazgo REABIERTO o CERRADO preservando fechaCierre sin lanzar excepciones")
    void reconstituirRestauraEstadoSinTransiciones() {
        HallazgoId id = HallazgoId.nuevo();
        PlanRemediacion plan = crearPlanValido();
        LocalDate fechaCierreHistorica = LocalDate.of(2026, 9, 25);

        HallazgoAuditoria cerrado = HallazgoAuditoria.reconstituir(
                id, "Titulo", "Desc", "Area", Severidad.CRITICA,
                LocalDate.of(2026, 8, 1), EstadoHallazgo.CERRADO, plan, fechaCierreHistorica
        );
        assertEquals(EstadoHallazgo.CERRADO, cerrado.getEstado());
        assertEquals(fechaCierreHistorica, cerrado.getFechaCierre());

        HallazgoAuditoria reabierto = HallazgoAuditoria.reconstituir(
                id, "Titulo", "Desc", "Area", Severidad.CRITICA,
                LocalDate.of(2026, 8, 1), EstadoHallazgo.REABIERTO, plan, null
        );
        assertEquals(EstadoHallazgo.REABIERTO, reabierto.getEstado());
        assertNull(reabierto.getFechaCierre());
    }
}
