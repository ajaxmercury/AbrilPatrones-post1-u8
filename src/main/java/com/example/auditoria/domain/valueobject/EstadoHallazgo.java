package com.example.auditoria.domain.valueobject;

public enum EstadoHallazgo {
    ABIERTO,
    EN_REMEDIACION,
    CERRADO,
    REABIERTO;

    public boolean puedeTransicionarA(EstadoHallazgo nuevoEstado) {
        if (nuevoEstado == null) {
            return false;
        }
        return switch (this) {
            case ABIERTO -> nuevoEstado == EN_REMEDIACION;
            case EN_REMEDIACION -> nuevoEstado == CERRADO;
            case CERRADO -> nuevoEstado == REABIERTO;
            case REABIERTO -> nuevoEstado == EN_REMEDIACION;
        };
    }
}
