package com.example.auditoria.domain.entity;

import com.example.auditoria.domain.valueobject.EstadoHallazgo;
import com.example.auditoria.domain.valueobject.HallazgoId;
import com.example.auditoria.domain.valueobject.PlanRemediacion;
import com.example.auditoria.domain.valueobject.Severidad;
import com.example.auditoria.domain.valueobject.TransicionInvalidaException;

import java.time.LocalDate;
import java.util.Objects;

public class HallazgoAuditoria {

    private final HallazgoId id;
    private final String titulo;
    private final String descripcion;
    private final String area;
    private final Severidad severidad;
    private final LocalDate fechaDeteccion;
    private EstadoHallazgo estado;
    private PlanRemediacion planRemediacion;
    private LocalDate fechaCierre;

    public HallazgoAuditoria(
            HallazgoId id,
            String titulo,
            String descripcion,
            String area,
            Severidad severidad,
            LocalDate fechaDeteccion) {
        this.id = Objects.requireNonNull(id, "El identificador del hallazgo es obligatorio");
        if (titulo == null || titulo.isBlank()) {
            throw new IllegalArgumentException("El título del hallazgo es obligatorio");
        }
        if (area == null || area.isBlank()) {
            throw new IllegalArgumentException("El área del hallazgo es obligatoria");
        }
        this.severidad = Objects.requireNonNull(severidad, "La severidad del hallazgo es obligatoria");
        this.fechaDeteccion = Objects.requireNonNull(fechaDeteccion, "La fecha de detección del hallazgo es obligatoria");
        this.titulo = titulo.trim();
        this.descripcion = (descripcion != null) ? descripcion.trim() : "";
        this.area = area.trim();
        this.estado = EstadoHallazgo.ABIERTO;
        this.planRemediacion = null;
        this.fechaCierre = null;
    }

    private HallazgoAuditoria(
            HallazgoId id,
            String titulo,
            String descripcion,
            String area,
            Severidad severidad,
            LocalDate fechaDeteccion,
            EstadoHallazgo estado,
            PlanRemediacion planRemediacion,
            LocalDate fechaCierre) {
        this.id = Objects.requireNonNull(id, "El identificador del hallazgo es obligatorio");
        this.titulo = Objects.requireNonNull(titulo, "El título del hallazgo es obligatorio");
        this.descripcion = (descripcion != null) ? descripcion : "";
        this.area = Objects.requireNonNull(area, "El área del hallazgo es obligatoria");
        this.severidad = Objects.requireNonNull(severidad, "La severidad del hallazgo es obligatoria");
        this.fechaDeteccion = Objects.requireNonNull(fechaDeteccion, "La fecha de detección es obligatoria");
        this.estado = Objects.requireNonNull(estado, "El estado del hallazgo es obligatorio");
        this.planRemediacion = planRemediacion;
        this.fechaCierre = fechaCierre;
    }

    public static HallazgoAuditoria reconstituir(
            HallazgoId id,
            String titulo,
            String descripcion,
            String area,
            Severidad severidad,
            LocalDate fechaDeteccion,
            EstadoHallazgo estado,
            PlanRemediacion plan,
            LocalDate fechaCierre) {
        return new HallazgoAuditoria(
                id,
                titulo,
                descripcion,
                area,
                severidad,
                fechaDeteccion,
                estado,
                plan,
                fechaCierre
        );
    }

    public EstadoHallazgo iniciarRemediacion(PlanRemediacion plan) {
        Objects.requireNonNull(plan, "El plan de remediación es obligatorio");
        if (!this.estado.puedeTransicionarA(EstadoHallazgo.EN_REMEDIACION)) {
            throw new TransicionInvalidaException(this.estado, EstadoHallazgo.EN_REMEDIACION);
        }
        EstadoHallazgo estadoAnterior = this.estado;
        this.planRemediacion = plan;
        this.estado = EstadoHallazgo.EN_REMEDIACION;
        return estadoAnterior;
    }

    public EstadoHallazgo cerrar() {
        if (this.planRemediacion == null) {
            throw new IllegalStateException("No se puede cerrar un hallazgo sin plan de remediación previo");
        }
        if (!this.estado.puedeTransicionarA(EstadoHallazgo.CERRADO)) {
            throw new TransicionInvalidaException(this.estado, EstadoHallazgo.CERRADO);
        }
        EstadoHallazgo estadoAnterior = this.estado;
        this.fechaCierre = LocalDate.now();
        this.estado = EstadoHallazgo.CERRADO;
        return estadoAnterior;
    }

    public EstadoHallazgo reabrir() {
        if (!this.estado.puedeTransicionarA(EstadoHallazgo.REABIERTO)) {
            throw new TransicionInvalidaException(this.estado, EstadoHallazgo.REABIERTO);
        }
        EstadoHallazgo estadoAnterior = this.estado;
        this.fechaCierre = null;
        this.estado = EstadoHallazgo.REABIERTO;
        return estadoAnterior;
    }

    public HallazgoId getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getArea() {
        return area;
    }

    public Severidad getSeveridad() {
        return severidad;
    }

    public LocalDate getFechaDeteccion() {
        return fechaDeteccion;
    }

    public EstadoHallazgo getEstado() {
        return estado;
    }

    public PlanRemediacion getPlanRemediacion() {
        return planRemediacion;
    }

    public LocalDate getFechaCierre() {
        return fechaCierre;
    }
}
