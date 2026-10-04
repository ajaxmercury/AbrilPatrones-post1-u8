package com.example.auditoria.usecase;

import com.example.auditoria.domain.valueobject.HallazgoId;
import com.example.auditoria.domain.valueobject.PlanRemediacion;

public interface IniciarRemediacionUseCase {

    void iniciarRemediacion(HallazgoId id, PlanRemediacion plan, String usuario);

    default void iniciarRemediacion(HallazgoId id, PlanRemediacion plan) {
        iniciarRemediacion(id, plan, "sistema");
    }
}
