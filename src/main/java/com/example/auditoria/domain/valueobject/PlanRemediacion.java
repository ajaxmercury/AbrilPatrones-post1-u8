package com.example.auditoria.domain.valueobject;

import java.time.LocalDate;
import java.util.Objects;

public record PlanRemediacion(String responsable, LocalDate fechaLimite, String notas) {

    public PlanRemediacion {
        if (responsable == null || responsable.isBlank()) {
            throw new IllegalArgumentException("El responsable del plan de remediación es obligatorio");
        }
        Objects.requireNonNull(fechaLimite, "La fecha límite del plan de remediación es obligatoria");
        notas = (notas != null) ? notas.trim() : "";
    }
}
