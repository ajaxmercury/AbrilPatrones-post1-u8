package com.example.auditoria.usecase.port;

import com.example.auditoria.domain.valueobject.HallazgoId;
import com.example.auditoria.usecase.CambioEstadoView;

import java.util.List;

public interface HistorialAuditoriaPort {

    void registrar(CambioEstadoView cambio);

    List<CambioEstadoView> listarPorHallazgo(HallazgoId hallazgoId);
}
