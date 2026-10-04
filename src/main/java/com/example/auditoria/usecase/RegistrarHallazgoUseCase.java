package com.example.auditoria.usecase;

import com.example.auditoria.domain.valueobject.HallazgoId;

public interface RegistrarHallazgoUseCase {

    HallazgoId registrar(RegistrarHallazgoCommand command);
}
