package com.example.auditoria.usecase;

import com.example.auditoria.domain.valueobject.HallazgoId;

public interface ReabrirHallazgoUseCase {

    void reabrir(HallazgoId id, String motivo, String usuario);

    default void reabrir(HallazgoId id, String motivo) {
        reabrir(id, motivo, "sistema");
    }
}
