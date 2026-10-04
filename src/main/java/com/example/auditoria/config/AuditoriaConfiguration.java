package com.example.auditoria.config;

import com.example.auditoria.usecase.CerrarHallazgoUseCase;
import com.example.auditoria.usecase.ConsultarHallazgoUseCase;
import com.example.auditoria.usecase.IniciarRemediacionUseCase;
import com.example.auditoria.usecase.ReabrirHallazgoUseCase;
import com.example.auditoria.usecase.RegistrarHallazgoUseCase;
import com.example.auditoria.usecase.impl.CerrarHallazgoService;
import com.example.auditoria.usecase.impl.ConsultarHallazgoService;
import com.example.auditoria.usecase.impl.IniciarRemediacionService;
import com.example.auditoria.usecase.impl.ReabrirHallazgoService;
import com.example.auditoria.usecase.impl.RegistrarHallazgoService;
import com.example.auditoria.usecase.port.HallazgoRepositoryPort;
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
            TransactionTemplate transactionTemplate) {
        IniciarRemediacionService delegate = new IniciarRemediacionService(repository);
        return (id, plan, usuario) -> transactionTemplate.executeWithoutResult(status ->
                delegate.iniciarRemediacion(id, plan, usuario)
        );
    }

    @Bean
    public CerrarHallazgoUseCase cerrarHallazgoUseCase(
            HallazgoRepositoryPort repository,
            TransactionTemplate transactionTemplate) {
        CerrarHallazgoService delegate = new CerrarHallazgoService(repository);
        return (id, usuario) -> transactionTemplate.executeWithoutResult(status ->
                delegate.cerrar(id, usuario)
        );
    }

    @Bean
    public ReabrirHallazgoUseCase reabrirHallazgoUseCase(
            HallazgoRepositoryPort repository,
            TransactionTemplate transactionTemplate) {
        ReabrirHallazgoService delegate = new ReabrirHallazgoService(repository);
        return (id, motivo, usuario) -> transactionTemplate.executeWithoutResult(status ->
                delegate.reabrir(id, motivo, usuario)
        );
    }

    @Bean
    public ConsultarHallazgoUseCase consultarHallazgoUseCase(HallazgoRepositoryPort repository) {
        return new ConsultarHallazgoService(repository);
    }
}
