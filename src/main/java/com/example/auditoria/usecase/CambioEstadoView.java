package com.example.auditoria.usecase;

import java.time.LocalDateTime;
import java.util.UUID;

public record CambioEstadoView(
        UUID id,
        UUID hallazgoId,
        String estadoAnterior,
        String estadoNuevo,
        String usuario,
        String motivo,
        LocalDateTime fechaHora
) {
}
