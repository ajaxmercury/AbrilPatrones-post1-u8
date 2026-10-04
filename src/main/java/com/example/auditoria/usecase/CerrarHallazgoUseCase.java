package com.example.auditoria.usecase;

import com.example.auditoria.domain.valueobject.HallazgoId;

public interface CerrarHallazgoUseCase {

    void cerrar(HallazgoId id, String usuario);

    default void cerrar(HallazgoId id) {
        cerrar(id, "sistema");
    }
}
