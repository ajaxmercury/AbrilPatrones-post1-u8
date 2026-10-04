package com.example.auditoria.usecase;

import com.example.auditoria.domain.valueobject.HallazgoId;

import java.util.List;

public interface ConsultarHistorialUseCase {

    List<CambioEstadoView> listarPorHallazgo(HallazgoId hallazgoId);
}
