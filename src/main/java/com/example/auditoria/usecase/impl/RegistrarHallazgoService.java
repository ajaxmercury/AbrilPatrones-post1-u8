package com.example.auditoria.usecase.impl;

import com.example.auditoria.domain.entity.HallazgoAuditoria;
import com.example.auditoria.domain.valueobject.HallazgoId;
import com.example.auditoria.usecase.RegistrarHallazgoCommand;
import com.example.auditoria.usecase.RegistrarHallazgoUseCase;
import com.example.auditoria.usecase.port.HallazgoRepositoryPort;

import java.util.Objects;

public class RegistrarHallazgoService implements RegistrarHallazgoUseCase {

    private final HallazgoRepositoryPort repository;

    public RegistrarHallazgoService(HallazgoRepositoryPort repository) {
        this.repository = Objects.requireNonNull(repository, "El repositorio no puede ser nulo");
    }

    @Override
    public HallazgoId registrar(RegistrarHallazgoCommand command) {
        Objects.requireNonNull(command, "El comando de registro no puede ser nulo");
        HallazgoId nuevoId = HallazgoId.nuevo();
        HallazgoAuditoria hallazgo = new HallazgoAuditoria(
                nuevoId,
                command.titulo(),
                command.descripcion(),
                command.area(),
                command.severidad(),
                command.fechaDeteccion()
        );
        repository.guardar(hallazgo);
        return nuevoId;
    }
}
