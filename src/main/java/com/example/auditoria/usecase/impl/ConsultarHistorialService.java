package com.example.auditoria.usecase.impl;

import com.example.auditoria.domain.valueobject.HallazgoId;
import com.example.auditoria.usecase.CambioEstadoView;
import com.example.auditoria.usecase.ConsultarHistorialUseCase;
import com.example.auditoria.usecase.port.HistorialAuditoriaPort;

import java.util.List;
import java.util.Objects;

public class ConsultarHistorialService implements ConsultarHistorialUseCase {

    private final HistorialAuditoriaPort historialPort;

    public ConsultarHistorialService(HistorialAuditoriaPort historialPort) {
        this.historialPort = Objects.requireNonNull(historialPort, "El puerto de historial no puede ser nulo");
    }

    @Override
    public List<CambioEstadoView> listarPorHallazgo(HallazgoId hallazgoId) {
        Objects.requireNonNull(hallazgoId, "El identificador del hallazgo es obligatorio");
        return historialPort.listarPorHallazgo(hallazgoId);
    }
}
