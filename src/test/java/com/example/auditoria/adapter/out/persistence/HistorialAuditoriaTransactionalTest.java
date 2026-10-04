package com.example.auditoria.adapter.out.persistence;

import com.example.auditoria.domain.entity.HallazgoAuditoria;
import com.example.auditoria.domain.valueobject.EstadoHallazgo;
import com.example.auditoria.domain.valueobject.HallazgoId;
import com.example.auditoria.domain.valueobject.PlanRemediacion;
import com.example.auditoria.domain.valueobject.Severidad;
import com.example.auditoria.usecase.CambioEstadoView;
import com.example.auditoria.usecase.CerrarHallazgoUseCase;
import com.example.auditoria.usecase.IniciarRemediacionUseCase;
import com.example.auditoria.usecase.port.HallazgoRepositoryPort;
import com.example.auditoria.usecase.port.HistorialAuditoriaPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@DisplayName("Pruebas de atomicidad transaccional de la bitácora de auditoría")
class HistorialAuditoriaTransactionalTest {

    @Autowired
    private HallazgoRepositoryPort repository;

    @Autowired
    private HistorialAuditoriaPort historialPort;

    @Autowired
    private IniciarRemediacionUseCase iniciarRemediacionUseCase;

    @Autowired
    private CerrarHallazgoUseCase cerrarUseCase;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Test
    @DisplayName("Transición exitosa persiste el cambio de estado y exactamente un registro en la bitácora")
    void transicionExitosaPersisteHallazgoYBitacora() {
        HallazgoId id = HallazgoId.nuevo();
        HallazgoAuditoria hallazgo = new HallazgoAuditoria(
                id,
                "Falta de control de accesos",
                "Sin autenticación multifactor",
                "Seguridad",
                Severidad.CRITICA,
                LocalDate.of(2026, 8, 1)
        );
        repository.guardar(hallazgo);

        PlanRemediacion plan = new PlanRemediacion("Oficial de Seguridad", LocalDate.of(2026, 9, 30), "Desplegar MFA");
        iniciarRemediacionUseCase.iniciarRemediacion(id, plan, "auditor_seguridad");

        HallazgoAuditoria actualizado = repository.buscarPorId(id).orElseThrow();
        assertEquals(EstadoHallazgo.EN_REMEDIACION, actualizado.getEstado());

        List<CambioEstadoView> historial = historialPort.listarPorHallazgo(id);
        assertEquals(1, historial.size());
        CambioEstadoView cambio = historial.get(0);
        assertEquals("ABIERTO", cambio.estadoAnterior());
        assertEquals("EN_REMEDIACION", cambio.estadoNuevo());
        assertEquals("auditor_seguridad", cambio.usuario());
    }

    @Test
    @DisplayName("Si falla el registro en la bitácora, se revierte el cambio de estado (Atomicidad)")
    void falloEnBitacoraRevierteCambioDeEstado() {
        HallazgoId id = HallazgoId.nuevo();
        HallazgoAuditoria hallazgo = new HallazgoAuditoria(
                id,
                "Inconsistencia en conciliación",
                "Diferencias en saldos",
                "Finanzas",
                Severidad.ALTA,
                LocalDate.of(2026, 8, 5)
        );
        repository.guardar(hallazgo);

        // Simulamos un puerto con falla forzada en registrar
        HistorialAuditoriaPort puertoConFalla = mock(HistorialAuditoriaPort.class);
        doThrow(new RuntimeException("Simulacion de fallo en base de datos de bitacora"))
                .when(puertoConFalla).registrar(any());

        // Decorador transaccional equivalente al de AuditoriaConfiguration
        IniciarRemediacionUseCase useCaseConFalla = (hallazgoId, plan, usuario) ->
                transactionTemplate.executeWithoutResult(status -> {
                    com.example.auditoria.usecase.impl.IniciarRemediacionService service =
                            new com.example.auditoria.usecase.impl.IniciarRemediacionService(repository, puertoConFalla);
                    service.iniciarRemediacion(hallazgoId, plan, usuario);
                });

        PlanRemediacion plan = new PlanRemediacion("Jefe Finanzas", LocalDate.of(2026, 9, 15), "Ajustes contables");

        assertThrows(RuntimeException.class, () ->
                useCaseConFalla.iniciarRemediacion(id, plan, "auditor_fallido")
        );

        // Verificamos que el hallazgo NO quedó en EN_REMEDIACION, sino que se revirtió a ABIERTO
        HallazgoAuditoria noModificado = repository.buscarPorId(id).orElseThrow();
        assertEquals(EstadoHallazgo.ABIERTO, noModificado.getEstado(),
                "El estado debe permanecer en ABIERTO debido al rollback transaccional");
    }
}
