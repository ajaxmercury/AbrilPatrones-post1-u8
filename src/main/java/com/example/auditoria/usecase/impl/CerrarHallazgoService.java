package com.example.auditoria.usecase.impl;

import com.example.auditoria.domain.entity.HallazgoAuditoria;
import com.example.auditoria.domain.valueobject.EstadoHallazgo;
import com.example.auditoria.domain.valueobject.HallazgoId;
import com.example.auditoria.usecase.CambioEstadoView;
import com.example.auditoria.usecase.CerrarHallazgoUseCase;
import com.example.auditoria.usecase.HallazgoNotFoundException;
import com.example.auditoria.usecase.port.HallazgoRepositoryPort;
import com.example.auditoria.usecase.port.HistorialAuditoriaPort;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class CerrarHallazgoService implements CerrarHallazgoUseCase {

    private final HallazgoRepositoryPort repository;
    private final HistorialAuditoriaPort historialPort;

    public CerrarHallazgoService(HallazgoRepositoryPort repository, HistorialAuditoriaPort historialPort) {
        this.repository = Objects.requireNonNull(repository, "El repositorio no puede ser nulo");
        this.historialPort = Objects.requireNonNull(historialPort, "El puerto de historial no puede ser nulo");
    }

    @Override
    public void cerrar(HallazgoId id, String usuario) {
        Objects.requireNonNull(id, "El identificador del hallazgo es obligatorio");

        HallazgoAuditoria hallazgo = repository.buscarPorId(id)
                .orElseThrow(() -> new HallazgoNotFoundException(id));

        EstadoHallazgo estadoAnterior = hallazgo.cerrar();
        repository.guardar(hallazgo);

        historialPort.registrar(new CambioEstadoView(
                UUID.randomUUID(),
                id.valor(),
                estadoAnterior.name(),
                hallazgo.getEstado().name(),
                (usuario != null && !usuario.isBlank()) ? usuario : "sistema",
                "Cierre de hallazgo tras verificar cumplimiento del plan de remediación",
                LocalDateTime.now()
        ));
    }
}
