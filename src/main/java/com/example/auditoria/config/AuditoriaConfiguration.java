package com.example.auditoria.config;

import com.example.auditoria.usecase.CerrarHallazgoUseCase;
import com.example.auditoria.usecase.ConsultarHallazgoUseCase;
import com.example.auditoria.usecase.ConsultarHistorialUseCase;
import com.example.auditoria.usecase.IniciarRemediacionUseCase;
import com.example.auditoria.usecase.ObtenerDashboardAuditoriaUseCase;
import com.example.auditoria.usecase.ReabrirHallazgoUseCase;
import com.example.auditoria.usecase.RegistrarHallazgoUseCase;
import com.example.auditoria.usecase.impl.CerrarHallazgoService;
import com.example.auditoria.usecase.impl.ConsultarHallazgoService;
import com.example.auditoria.usecase.impl.ConsultarHistorialService;
import com.example.auditoria.usecase.impl.IniciarRemediacionService;
import com.example.auditoria.usecase.impl.ObtenerDashboardAuditoriaService;
import com.example.auditoria.usecase.impl.ReabrirHallazgoService;
import com.example.auditoria.usecase.impl.RegistrarHallazgoService;
import com.example.auditoria.usecase.port.HallazgoRepositoryPort;
import com.example.auditoria.usecase.port.HistorialAuditoriaPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

@Configuration
public class AuditoriaConfiguration {

    @Bean
    public TransactionTemplate transactionTemplate(PlatformTransactionManager transactionManager) {
        return new TransactionTemplate(transactionManager);
    }

    @Bean
    public RegistrarHallazgoUseCase registrarHallazgoUseCase(HallazgoRepositoryPort repository) {
        return new RegistrarHallazgoService(repository);
    }

    @Bean
    public IniciarRemediacionUseCase iniciarRemediacionUseCase(
            HallazgoRepositoryPort repository,
            HistorialAuditoriaPort historialPort,
            TransactionTemplate transactionTemplate) {
        IniciarRemediacionService delegate = new IniciarRemediacionService(repository, historialPort);
        return (id, plan, usuario) -> transactionTemplate.executeWithoutResult(status ->
                delegate.iniciarRemediacion(id, plan, usuario)
        );
    }

    @Bean
    public CerrarHallazgoUseCase cerrarHallazgoUseCase(
            HallazgoRepositoryPort repository,
            HistorialAuditoriaPort historialPort,
            TransactionTemplate transactionTemplate) {
        CerrarHallazgoService delegate = new CerrarHallazgoService(repository, historialPort);
        return (id, usuario) -> transactionTemplate.executeWithoutResult(status ->
                delegate.cerrar(id, usuario)
        );
    }

    @Bean
    public ReabrirHallazgoUseCase reabrirHallazgoUseCase(
            HallazgoRepositoryPort repository,
            HistorialAuditoriaPort historialPort,
            TransactionTemplate transactionTemplate) {
        ReabrirHallazgoService delegate = new ReabrirHallazgoService(repository, historialPort);
        return (id, motivo, usuario) -> transactionTemplate.executeWithoutResult(status ->
                delegate.reabrir(id, motivo, usuario)
        );
    }

    @Bean
    public ConsultarHallazgoUseCase consultarHallazgoUseCase(HallazgoRepositoryPort repository) {
        return new ConsultarHallazgoService(repository);
    }

    @Bean
    public ConsultarHistorialUseCase consultarHistorialUseCase(HistorialAuditoriaPort historialPort) {
        return new ConsultarHistorialService(historialPort);
    }

    @Bean
    public ObtenerDashboardAuditoriaUseCase obtenerDashboardAuditoriaUseCase(HallazgoRepositoryPort repository) {
        return new ObtenerDashboardAuditoriaService(repository);
    }
}
