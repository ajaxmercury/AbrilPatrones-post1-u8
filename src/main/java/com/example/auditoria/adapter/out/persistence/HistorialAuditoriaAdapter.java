package com.example.auditoria.adapter.out.persistence;

import com.example.auditoria.domain.valueobject.HallazgoId;
import com.example.auditoria.usecase.CambioEstadoView;
import com.example.auditoria.usecase.port.HistorialAuditoriaPort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
public class HistorialAuditoriaAdapter implements HistorialAuditoriaPort {

    private final HistorialCambioEstadoJpaRepository jpaRepository;

    public HistorialAuditoriaAdapter(HistorialCambioEstadoJpaRepository jpaRepository) {
        this.jpaRepository = Objects.requireNonNull(jpaRepository, "El repositorio de historial JPA no puede ser nulo");
    }

    @Override
    public void registrar(CambioEstadoView cambio) {
        Objects.requireNonNull(cambio, "El registro de cambio de estado no puede ser nulo");
        HistorialCambioEstadoJpaEntity entity = new HistorialCambioEstadoJpaEntity(
                cambio.id(),
                cambio.hallazgoId(),
                cambio.estadoAnterior(),
                cambio.estadoNuevo(),
                cambio.usuario(),
                cambio.motivo(),
                cambio.fechaHora()
        );
        jpaRepository.save(entity);
    }

    @Override
    public List<CambioEstadoView> listarPorHallazgo(HallazgoId hallazgoId) {
        Objects.requireNonNull(hallazgoId, "El identificador del hallazgo no puede ser nulo");
        return jpaRepository.findByHallazgoIdOrderByFechaHoraAsc(hallazgoId.valor())
                .stream()
                .map(this::toView)
                .toList();
    }

    private CambioEstadoView toView(HistorialCambioEstadoJpaEntity entity) {
        return new CambioEstadoView(
                entity.getId(),
                entity.getHallazgoId(),
                entity.getEstadoAnterior(),
                entity.getEstadoNuevo(),
                entity.getUsuario(),
                entity.getMotivo(),
                entity.getFechaHora()
        );
    }
}
