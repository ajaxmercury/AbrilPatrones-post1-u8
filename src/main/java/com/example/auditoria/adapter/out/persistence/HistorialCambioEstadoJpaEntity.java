package com.example.auditoria.adapter.out.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "historial_cambios_estado")
public class HistorialCambioEstadoJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "hallazgo_id", nullable = false, updatable = false)
    private UUID hallazgoId;

    @Column(name = "estado_anterior", nullable = false, updatable = false, length = 30)
    private String estadoAnterior;

    @Column(name = "estado_nuevo", nullable = false, updatable = false, length = 30)
    private String estadoNuevo;

    @Column(name = "usuario", nullable = false, updatable = false, length = 100)
    private String usuario;

    @Column(name = "motivo", updatable = false, length = 2000)
    private String motivo;

    @Column(name = "fecha_hora", nullable = false, updatable = false)
    private LocalDateTime fechaHora;

    public HistorialCambioEstadoJpaEntity() {
    }

    public HistorialCambioEstadoJpaEntity(
            UUID id,
            UUID hallazgoId,
            String estadoAnterior,
            String estadoNuevo,
            String usuario,
            String motivo,
            LocalDateTime fechaHora) {
        this.id = id;
        this.hallazgoId = hallazgoId;
        this.estadoAnterior = estadoAnterior;
        this.estadoNuevo = estadoNuevo;
        this.usuario = usuario;
        this.motivo = motivo;
        this.fechaHora = fechaHora;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getHallazgoId() {
        return hallazgoId;
    }

    public void setHallazgoId(UUID hallazgoId) {
        this.hallazgoId = hallazgoId;
    }

    public String getEstadoAnterior() {
        return estadoAnterior;
    }

    public void setEstadoAnterior(String estadoAnterior) {
        this.estadoAnterior = estadoAnterior;
    }

    public String getEstadoNuevo() {
        return estadoNuevo;
    }

    public void setEstadoNuevo(String estadoNuevo) {
        this.estadoNuevo = estadoNuevo;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }
}
