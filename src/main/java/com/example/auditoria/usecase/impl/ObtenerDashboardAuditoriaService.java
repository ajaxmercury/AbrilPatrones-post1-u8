package com.example.auditoria.usecase.impl;

import com.example.auditoria.usecase.ConteoCategoria;
import com.example.auditoria.usecase.DashboardAuditoriaView;
import com.example.auditoria.usecase.ObtenerDashboardAuditoriaUseCase;
import com.example.auditoria.usecase.PromedioCategoria;
import com.example.auditoria.usecase.port.HallazgoRepositoryPort;

import java.util.List;
import java.util.Objects;

public class ObtenerDashboardAuditoriaService implements ObtenerDashboardAuditoriaUseCase {

    private final HallazgoRepositoryPort repository;

    public ObtenerDashboardAuditoriaService(HallazgoRepositoryPort repository) {
        this.repository = Objects.requireNonNull(repository, "El repositorio no puede ser nulo");
    }

    @Override
    public DashboardAuditoriaView obtenerDashboard() {
        List<ConteoCategoria> porSeveridad = repository.contarPorSeveridad();
        List<ConteoCategoria> porEstado = repository.contarPorEstado();
        List<PromedioCategoria> promedios = repository.promedioDiasCierrePorArea();

        return new DashboardAuditoriaView(porSeveridad, porEstado, promedios);
    }
}
