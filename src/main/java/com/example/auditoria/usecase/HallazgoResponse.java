package com.example.auditoria.usecase;

import com.example.auditoria.domain.entity.HallazgoAuditoria;

import java.time.LocalDate;
import java.util.UUID;

public record HallazgoResponse(
        UUID id,
        String titulo,
        String descripcion,
        String area,
        String severidad,
        LocalDate fechaDeteccion,
        String estado,
        PlanRemediacionResponse planRemediacion,
        LocalDate fechaCierre
) {
    public static HallazgoResponse de(HallazgoAuditoria h) {
        PlanRemediacionResponse planResp = null;
        if (h.getPlanRemediacion() != null) {
            planResp = new PlanRemediacionResponse(
                    h.getPlanRemediacion().responsable(),
                    h.getPlanRemediacion().fechaLimite(),
                    h.getPlanRemediacion().notas()
            );
        }
        return new HallazgoResponse(
                h.getId().valor(),
                h.getTitulo(),
                h.getDescripcion(),
                h.getArea(),
                h.getSeveridad().name(),
                h.getFechaDeteccion(),
                h.getEstado().name(),
                planResp,
                h.getFechaCierre()
        );
    }
}
