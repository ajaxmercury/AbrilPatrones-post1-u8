package com.example.auditoria.usecase.impl;

import com.example.auditoria.domain.entity.HallazgoAuditoria;
import com.example.auditoria.domain.valueobject.HallazgoId;
import com.example.auditoria.usecase.CerrarHallazgoUseCase;
import com.example.auditoria.usecase.HallazgoNotFoundException;
import com.example.auditoria.usecase.port.HallazgoRepositoryPort;

import java.util.Objects;

public class CerrarHallazgoService implements CerrarHallazgoUseCase {

    private final HallazgoRepositoryPort repository;

    public CerrarHallazgoService(HallazgoRepositoryPort repository) {
        this.repository = Objects.requireNonNull(repository, "El repositorio no puede ser nulo");
    }

    @Override
    public void cerrar(HallazgoId id, String usuario) {
        Objects.requireNonNull(id, "El identificador del hallazgo es obligatorio");

        HallazgoAuditoria hallazgo = repository.buscarPorId(id)
                .orElseThrow(() -> new HallazgoNotFoundException(id));

        hallazgo.cerrar();
        repository.guardar(hallazgo);
    }
}
