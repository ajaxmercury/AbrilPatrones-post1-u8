package com.example.auditoria.usecase.impl;

import com.example.auditoria.domain.entity.HallazgoAuditoria;
import com.example.auditoria.domain.valueobject.EstadoHallazgo;
import com.example.auditoria.domain.valueobject.HallazgoId;
import com.example.auditoria.usecase.CambioEstadoView;
import com.example.auditoria.usecase.HallazgoNotFoundException;
import com.example.auditoria.usecase.ReabrirHallazgoUseCase;
import com.example.auditoria.usecase.port.HallazgoRepositoryPort;
import com.example.auditoria.usecase.port.HistorialAuditoriaPort;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class ReabrirHallazgoService implements ReabrirHallazgoUseCase {

    private final HallazgoRepositoryPort repository;
    private final HistorialAuditoriaPort historialPort;

    public ReabrirHallazgoService(HallazgoRepositoryPort repository, HistorialAuditoriaPort historialPort) {
        this.repository = Objects.requireNonNull(repository, "El repositorio no puede ser nulo");
        this.historialPort = Objects.requireNonNull(historialPort, "El puerto de historial no puede ser nulo");
    }

    @Override
    public void reabrir(HallazgoId id, String motivo, String usuario) {
        Objects.requireNonNull(id, "El identificador del hallazgo es obligatorio");
        if (motivo == null || motivo.isBlank()) {
            throw new IllegalArgumentException("El motivo de reapertura es obligatorio");
        }

        HallazgoAuditoria hallazgo = repository.buscarPorId(id)
                .orElseThrow(() -> new HallazgoNotFoundException(id));

        EstadoHallazgo estadoAnterior = hallazgo.reabrir();
        repository.guardar(hallazgo);

        historialPort.registrar(new CambioEstadoView(
                UUID.randomUUID(),
                id.valor(),
                estadoAnterior.name(),
                hallazgo.getEstado().name(),
                (usuario != null && !usuario.isBlank()) ? usuario : "sistema",
                motivo.trim(),
                LocalDateTime.now()
        ));
    }
}
