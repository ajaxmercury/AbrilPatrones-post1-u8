package com.example.auditoria.adapter.out.persistence;

import com.example.auditoria.domain.valueobject.EstadoHallazgo;
import com.example.auditoria.domain.valueobject.Severidad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface HallazgoJpaRepository extends JpaRepository<HallazgoJpaEntity, UUID> {

    interface ConteoSeveridadProjection {
        Severidad getSeveridad();
        long getTotal();
    }

    interface ConteoEstadoProjection {
        EstadoHallazgo getEstado();
        long getTotal();
    }

    interface PromedioCierreAreaProjection {
        String getArea();
        Double getPromedioDias();
    }

    @Query("SELECT h.severidad AS severidad, COUNT(h) AS total FROM HallazgoJpaEntity h GROUP BY h.severidad")
    List<ConteoSeveridadProjection> contarPorSeveridad();

    @Query("SELECT h.estado AS estado, COUNT(h) AS total FROM HallazgoJpaEntity h GROUP BY h.estado")
    List<ConteoEstadoProjection> contarPorEstado();

    @Query(value = "SELECT area AS area, CAST(AVG(DATEDIFF('DAY', fecha_deteccion, fecha_cierre)) AS DOUBLE) AS promedioDias " +
            "FROM hallazgos_auditoria " +
            "WHERE estado = 'CERRADO' " +
            "GROUP BY area", nativeQuery = true)
    List<PromedioCierreAreaProjection> promedioDiasCierrePorArea();
}
