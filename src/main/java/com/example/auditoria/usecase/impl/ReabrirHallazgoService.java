package com.example.auditoria.usecase.impl;

import com.example.auditoria.domain.entity.HallazgoAuditoria;
import com.example.auditoria.domain.valueobject.HallazgoId;
import com.example.auditoria.usecase.HallazgoNotFoundException;
import com.example.auditoria.usecase.ReabrirHallazgoUseCase;
import com.example.auditoria.usecase.port.HallazgoRepositoryPort;

import java.util.Objects;

public class ReabrirHallazgoService implements ReabrirHallazgoUseCase {

    private final HallazgoRepositoryPort repository;

    public ReabrirHallazgoService(HallazgoRepositoryPort repository) {
        this.repository = Objects.requireNonNull(repository, "El repositorio no puede ser nulo");
    }

    @Override
    public void reabrir(HallazgoId id, String motivo, String usuario) {
        Objects.requireNonNull(id, "El identificador del hallazgo es obligatorio");
        if (motivo == null || motivo.isBlank()) {
            throw new IllegalArgumentException("El motivo de reapertura es obligatorio");
        }

        HallazgoAuditoria hallazgo = repository.buscarPorId(id)
                .orElseThrow(() -> new HallazgoNotFoundException(id));

        hallazgo.reabrir();
        repository.guardar(hallazgo);
    }
}
