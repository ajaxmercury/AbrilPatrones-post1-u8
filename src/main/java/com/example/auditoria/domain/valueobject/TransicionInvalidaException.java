package com.example.auditoria.domain.valueobject;

public class TransicionInvalidaException extends RuntimeException {

    public TransicionInvalidaException(EstadoHallazgo actual, EstadoHallazgo objetivo) {
        super(String.format("Transición de estado inválida: no es posible pasar de %s a %s", actual, objetivo));
    }

    public TransicionInvalidaException(String mensaje) {
        super(mensaje);
    }
}
