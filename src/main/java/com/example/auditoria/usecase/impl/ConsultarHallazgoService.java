package com.example.auditoria.usecase.impl;

import com.example.auditoria.domain.valueobject.HallazgoId;
import com.example.auditoria.usecase.ConsultarHallazgoUseCase;
import com.example.auditoria.usecase.HallazgoNotFoundException;
import com.example.auditoria.usecase.HallazgoResponse;
import com.example.auditoria.usecase.port.HallazgoRepositoryPort;

import java.util.List;
import java.util.Objects;

public class ConsultarHallazgoService implements ConsultarHallazgoUseCase {

    private final HallazgoRepositoryPort repository;

    public ConsultarHallazgoService(HallazgoRepositoryPort repository) {
        this.repository = Objects.requireNonNull(repository, "El repositorio no puede ser nulo");
    }

    @Override
    public HallazgoResponse buscarPorId(HallazgoId id) {
        Objects.requireNonNull(id, "El identificador del hallazgo es obligatorio");
        return repository.buscarPorId(id)
                .map(HallazgoResponse::de)
                .orElseThrow(() -> new HallazgoNotFoundException(id));
    }

    @Override
    public List<HallazgoResponse> listarTodos() {
        return repository.buscarTodos()
                .stream()
                .map(HallazgoResponse::de)
                .toList();
    }
}
