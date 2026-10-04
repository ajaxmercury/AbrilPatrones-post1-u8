package com.example.auditoria.usecase;

import com.example.auditoria.domain.valueobject.HallazgoId;

public class HallazgoNotFoundException extends RuntimeException {

    public HallazgoNotFoundException(HallazgoId id) {
        super(String.format("No se encontró el hallazgo de auditoría con identificador %s", id.valor()));
    }

    public HallazgoNotFoundException(String mensaje) {
        super(mensaje);
    }
}
