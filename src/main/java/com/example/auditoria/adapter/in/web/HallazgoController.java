package com.example.auditoria.adapter.in.web;

import com.example.auditoria.adapter.in.web.dto.IniciarRemediacionRequest;
import com.example.auditoria.adapter.in.web.dto.ReabrirRequest;
import com.example.auditoria.adapter.in.web.dto.RegistrarHallazgoRequest;
import com.example.auditoria.adapter.in.web.dto.RegistroResponse;
import com.example.auditoria.domain.valueobject.HallazgoId;
import com.example.auditoria.domain.valueobject.PlanRemediacion;
import com.example.auditoria.usecase.CerrarHallazgoUseCase;
import com.example.auditoria.usecase.ConsultarHallazgoUseCase;
import com.example.auditoria.usecase.HallazgoResponse;
import com.example.auditoria.usecase.IniciarRemediacionUseCase;
import com.example.auditoria.usecase.ReabrirHallazgoUseCase;
import com.example.auditoria.usecase.RegistrarHallazgoCommand;
import com.example.auditoria.usecase.RegistrarHallazgoUseCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/hallazgos")
public class HallazgoController {

    private final RegistrarHallazgoUseCase registrarUseCase;
    private final IniciarRemediacionUseCase iniciarRemediacionUseCase;
    private final CerrarHallazgoUseCase cerrarUseCase;
    private final ReabrirHallazgoUseCase reabrirUseCase;
    private final ConsultarHallazgoUseCase consultarUseCase;

    public HallazgoController(
            RegistrarHallazgoUseCase registrarUseCase,
            IniciarRemediacionUseCase iniciarRemediacionUseCase,
            CerrarHallazgoUseCase cerrarUseCase,
            ReabrirHallazgoUseCase reabrirUseCase,
            ConsultarHallazgoUseCase consultarUseCase) {
        this.registrarUseCase = registrarUseCase;
        this.iniciarRemediacionUseCase = iniciarRemediacionUseCase;
        this.cerrarUseCase = cerrarUseCase;
        this.reabrirUseCase = reabrirUseCase;
        this.consultarUseCase = consultarUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RegistroResponse registrar(@Valid @RequestBody RegistrarHallazgoRequest request) {
        RegistrarHallazgoCommand command = new RegistrarHallazgoCommand(
                request.titulo(),
                request.descripcion(),
                request.area(),
                request.severidad(),
                request.fechaDeteccion()
        );
        HallazgoId nuevoId = registrarUseCase.registrar(command);
        return new RegistroResponse(nuevoId.valor());
    }

    @PatchMapping("/{id}/iniciar-remediacion")
    public ResponseEntity<Map<String, Object>> iniciarRemediacion(
            @PathVariable UUID id,
            @Valid @RequestBody IniciarRemediacionRequest request,
            @RequestHeader(value = "X-Usuario", defaultValue = "sistema") String usuario) {
        PlanRemediacion plan = new PlanRemediacion(
                request.responsable(),
                request.fechaLimite(),
                request.notas()
        );
        iniciarRemediacionUseCase.iniciarRemediacion(HallazgoId.de(id), plan, usuario);
        return ResponseEntity.ok(Map.of(
                "mensaje", "Plan de remediación iniciado exitosamente",
                "hallazgoId", id,
                "estado", "EN_REMEDIACION"
        ));
    }

    @PatchMapping("/{id}/cerrar")
    public ResponseEntity<Map<String, Object>> cerrar(
            @PathVariable UUID id,
            @RequestHeader(value = "X-Usuario", defaultValue = "sistema") String usuario) {
        cerrarUseCase.cerrar(HallazgoId.de(id), usuario);
        return ResponseEntity.ok(Map.of(
                "mensaje", "Hallazgo cerrado exitosamente",
                "hallazgoId", id,
                "estado", "CERRADO"
        ));
    }

    @PatchMapping("/{id}/reabrir")
    public ResponseEntity<Map<String, Object>> reabrir(
            @PathVariable UUID id,
            @Valid @RequestBody ReabrirRequest request,
            @RequestHeader(value = "X-Usuario", defaultValue = "sistema") String usuario) {
        reabrirUseCase.reabrir(HallazgoId.de(id), request.motivo(), usuario);
        return ResponseEntity.ok(Map.of(
                "mensaje", "Hallazgo reabierto exitosamente",
                "hallazgoId", id,
                "estado", "REABIERTO"
        ));
    }

    @GetMapping("/{id}")
    public ResponseEntity<HallazgoResponse> buscarPorId(@PathVariable UUID id) {
        HallazgoResponse response = consultarUseCase.buscarPorId(HallazgoId.de(id));
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<HallazgoResponse>> listarTodos() {
        List<HallazgoResponse> list = consultarUseCase.listarTodos();
        return ResponseEntity.ok(list);
    }
}
