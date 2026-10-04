package com.example.auditoria.usecase;

import com.example.auditoria.domain.valueobject.Severidad;

import java.time.LocalDate;

public record RegistrarHallazgoCommand(
        String titulo,
        String descripcion,
        String area,
        Severidad severidad,
        LocalDate fechaDeteccion
) {
}
