package com.example.auditoria.usecase.impl;

import com.example.auditoria.domain.entity.HallazgoAuditoria;
import com.example.auditoria.domain.valueobject.HallazgoId;
import com.example.auditoria.domain.valueobject.PlanRemediacion;
import com.example.auditoria.usecase.HallazgoNotFoundException;
import com.example.auditoria.usecase.IniciarRemediacionUseCase;
import com.example.auditoria.usecase.port.HallazgoRepositoryPort;

import java.util.Objects;

public class IniciarRemediacionService implements IniciarRemediacionUseCase {

    private final HallazgoRepositoryPort repository;

    public IniciarRemediacionService(HallazgoRepositoryPort repository) {
        this.repository = Objects.requireNonNull(repository, "El repositorio no puede ser nulo");
    }

    @Override
    public void iniciarRemediacion(HallazgoId id, PlanRemediacion plan, String usuario) {
        Objects.requireNonNull(id, "El identificador del hallazgo es obligatorio");
        Objects.requireNonNull(plan, "El plan de remediación es obligatorio");

        HallazgoAuditoria hallazgo = repository.buscarPorId(id)
                .orElseThrow(() -> new HallazgoNotFoundException(id));

        hallazgo.iniciarRemediacion(plan);
        repository.guardar(hallazgo);
    }
}
