package com.example.auditoria.adapter.in.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record IniciarRemediacionRequest(
        @NotBlank(message = "El responsable del plan de remediación es obligatorio")
        String responsable,

        @NotNull(message = "La fecha límite del plan de remediación es obligatoria")
        LocalDate fechaLimite,

        String notas
) {
}
