package com.example.auditoria.usecase;

import java.time.LocalDate;

public record PlanRemediacionResponse(
        String responsable,
        LocalDate fechaLimite,
        String notas
) {
}
