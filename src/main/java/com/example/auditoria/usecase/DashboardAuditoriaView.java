package com.example.auditoria.usecase;

import java.util.List;

public record DashboardAuditoriaView(
        List<ConteoCategoria> porSeveridad,
        List<ConteoCategoria> porEstado,
        List<PromedioCategoria> promedioDiasCierrePorArea
) {
}
