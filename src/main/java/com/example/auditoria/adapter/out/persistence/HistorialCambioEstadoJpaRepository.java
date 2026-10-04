package com.example.auditoria.adapter.out.persistence;

import org.springframework.data.repository.Repository;

import java.util.List;
import java.util.UUID;

public interface HistorialCambioEstadoJpaRepository extends Repository<HistorialCambioEstadoJpaEntity, UUID> {

    HistorialCambioEstadoJpaEntity save(HistorialCambioEstadoJpaEntity entity);

    List<HistorialCambioEstadoJpaEntity> findByHallazgoIdOrderByFechaHoraAsc(UUID hallazgoId);
}
