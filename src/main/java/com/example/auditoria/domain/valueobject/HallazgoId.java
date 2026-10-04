package com.example.auditoria.domain.valueobject;

import java.util.Objects;
import java.util.UUID;

public record HallazgoId(UUID valor) {

    public HallazgoId {
        Objects.requireNonNull(valor, "El valor del identificador de hallazgo no puede ser nulo");
    }

    public static HallazgoId nuevo() {
        return new HallazgoId(UUID.randomUUID());
    }

    public static HallazgoId de(UUID uuid) {
        return new HallazgoId(uuid);
    }

    public static HallazgoId de(String valor) {
        Objects.requireNonNull(valor, "El identificador de hallazgo no puede ser nulo");
        try {
            return new HallazgoId(UUID.fromString(valor.trim()));
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Identificador de hallazgo UUID no válido: " + valor, ex);
        }
    }

    @Override
    public String toString() {
        return valor.toString();
    }
}
