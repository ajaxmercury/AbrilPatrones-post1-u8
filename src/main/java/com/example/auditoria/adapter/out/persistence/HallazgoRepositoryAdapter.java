package com.example.auditoria.adapter.out.persistence;

import com.example.auditoria.domain.entity.HallazgoAuditoria;
import com.example.auditoria.domain.valueobject.HallazgoId;
import com.example.auditoria.domain.valueobject.PlanRemediacion;
import com.example.auditoria.usecase.ConteoCategoria;
import com.example.auditoria.usecase.PromedioCategoria;
import com.example.auditoria.usecase.port.HallazgoRepositoryPort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Component
public class HallazgoRepositoryAdapter implements HallazgoRepositoryPort {

    private final HallazgoJpaRepository jpaRepository;

    public HallazgoRepositoryAdapter(HallazgoJpaRepository jpaRepository) {
        this.jpaRepository = Objects.requireNonNull(jpaRepository, "El repositorio JPA no puede ser nulo");
    }

    @Override
    public HallazgoAuditoria guardar(HallazgoAuditoria hallazgo) {
        Objects.requireNonNull(hallazgo, "El hallazgo a guardar no puede ser nulo");
        HallazgoJpaEntity entity = toEntity(hallazgo);
        HallazgoJpaEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<HallazgoAuditoria> buscarPorId(HallazgoId id) {
        Objects.requireNonNull(id, "El identificador del hallazgo no puede ser nulo");
        return jpaRepository.findById(id.valor()).map(this::toDomain);
    }

    @Override
    public List<HallazgoAuditoria> buscarTodos() {
        return jpaRepository.findAll().stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public List<ConteoCategoria> contarPorSeveridad() {
        return jpaRepository.contarPorSeveridad().stream()
                .map(p -> new ConteoCategoria(p.getSeveridad().name(), p.getTotal()))
                .toList();
    }

    @Override
    public List<ConteoCategoria> contarPorEstado() {
        return jpaRepository.contarPorEstado().stream()
                .map(p -> new ConteoCategoria(p.getEstado().name(), p.getTotal()))
                .toList();
    }

    @Override
    public List<PromedioCategoria> promedioDiasCierrePorArea() {
        return jpaRepository.promedioDiasCierrePorArea().stream()
                .map(p -> new PromedioCategoria(p.getArea(), p.getPromedioDias() != null ? p.getPromedioDias() : 0.0))
                .toList();
    }

    private HallazgoJpaEntity toEntity(HallazgoAuditoria domain) {
        HallazgoJpaEntity entity = new HallazgoJpaEntity();
        entity.setId(domain.getId().valor());
        entity.setTitulo(domain.getTitulo());
        entity.setDescripcion(domain.getDescripcion());
        entity.setArea(domain.getArea());
        entity.setSeveridad(domain.getSeveridad());
        entity.setFechaDeteccion(domain.getFechaDeteccion());
        entity.setEstado(domain.getEstado());
        entity.setFechaCierre(domain.getFechaCierre());

        if (domain.getPlanRemediacion() != null) {
            entity.setPlanResponsable(domain.getPlanRemediacion().responsable());
            entity.setPlanFechaLimite(domain.getPlanRemediacion().fechaLimite());
            entity.setPlanNotas(domain.getPlanRemediacion().notas());
        } else {
            entity.setPlanResponsable(null);
            entity.setPlanFechaLimite(null);
            entity.setPlanNotas(null);
        }

        return entity;
    }

    private HallazgoAuditoria toDomain(HallazgoJpaEntity entity) {
        PlanRemediacion plan = null;
        if (entity.getPlanResponsable() != null && entity.getPlanFechaLimite() != null) {
            plan = new PlanRemediacion(
                    entity.getPlanResponsable(),
                    entity.getPlanFechaLimite(),
                    entity.getPlanNotas()
            );
        }

        return HallazgoAuditoria.reconstituir(
                HallazgoId.de(entity.getId()),
                entity.getTitulo(),
                entity.getDescripcion(),
                entity.getArea(),
                entity.getSeveridad(),
                entity.getFechaDeteccion(),
                entity.getEstado(),
                plan,
                entity.getFechaCierre()
        );
    }
}
