package com.example.auditoria.adapter.out.persistence;

import com.example.auditoria.domain.valueobject.EstadoHallazgo;
import com.example.auditoria.domain.valueobject.Severidad;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "hallazgos_auditoria")
public class HallazgoJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "titulo", nullable = false, length = 255)
    private String titulo;

    @Column(name = "descripcion", length = 2000)
    private String descripcion;

    @Column(name = "area", nullable = false, length = 100)
    private String area;

    @Enumerated(EnumType.STRING)
    @Column(name = "severidad", nullable = false, length = 20)
    private Severidad severidad;

    @Column(name = "fecha_deteccion", nullable = false)
    private LocalDate fechaDeteccion;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 20)
    private EstadoHallazgo estado;

    @Column(name = "fecha_cierre")
    private LocalDate fechaCierre;

    @Column(name = "plan_responsable", length = 150)
    private String planResponsable;

    @Column(name = "plan_fecha_limite")
    private LocalDate planFechaLimite;

    @Column(name = "plan_notas", length = 2000)
    private String planNotas;

    public HallazgoJpaEntity() {
    }

    public HallazgoJpaEntity(
            UUID id,
            String titulo,
            String descripcion,
            String area,
            Severidad severidad,
            LocalDate fechaDeteccion,
            EstadoHallazgo estado,
            LocalDate fechaCierre,
            String planResponsable,
            LocalDate planFechaLimite,
            String planNotas) {
        this.id = id;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.area = area;
        this.severidad = severidad;
        this.fechaDeteccion = fechaDeteccion;
        this.estado = estado;
        this.fechaCierre = fechaCierre;
        this.planResponsable = planResponsable;
        this.planFechaLimite = planFechaLimite;
        this.planNotas = planNotas;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public Severidad getSeveridad() {
        return severidad;
    }

    public void setSeveridad(Severidad severidad) {
        this.severidad = severidad;
    }

    public LocalDate getFechaDeteccion() {
        return fechaDeteccion;
    }

    public void setFechaDeteccion(LocalDate fechaDeteccion) {
        this.fechaDeteccion = fechaDeteccion;
    }

    public EstadoHallazgo getEstado() {
        return estado;
    }

    public void setEstado(EstadoHallazgo estado) {
        this.estado = estado;
    }

    public LocalDate getFechaCierre() {
        return fechaCierre;
    }

    public void setFechaCierre(LocalDate fechaCierre) {
        this.fechaCierre = fechaCierre;
    }

    public String getPlanResponsable() {
        return planResponsable;
    }

    public void setPlanResponsable(String planResponsable) {
        this.planResponsable = planResponsable;
    }

    public LocalDate getPlanFechaLimite() {
        return planFechaLimite;
    }

    public void setPlanFechaLimite(LocalDate planFechaLimite) {
        this.planFechaLimite = planFechaLimite;
    }

    public String getPlanNotas() {
        return planNotas;
    }

    public void setPlanNotas(String planNotas) {
        this.planNotas = planNotas;
    }
}
