package com.example.auditoria.adapter.in.web;

import com.example.auditoria.domain.valueobject.EstadoHallazgo;
import com.example.auditoria.domain.valueobject.HallazgoId;
import com.example.auditoria.domain.valueobject.TransicionInvalidaException;
import com.example.auditoria.usecase.CambioEstadoView;
import com.example.auditoria.usecase.CerrarHallazgoUseCase;
import com.example.auditoria.usecase.ConsultarHallazgoUseCase;
import com.example.auditoria.usecase.ConsultarHistorialUseCase;
import com.example.auditoria.usecase.ConteoCategoria;
import com.example.auditoria.usecase.DashboardAuditoriaView;
import com.example.auditoria.usecase.HallazgoResponse;
import com.example.auditoria.usecase.IniciarRemediacionUseCase;
import com.example.auditoria.usecase.ObtenerDashboardAuditoriaUseCase;
import com.example.auditoria.usecase.PlanRemediacionResponse;
import com.example.auditoria.usecase.PromedioCategoria;
import com.example.auditoria.usecase.ReabrirHallazgoUseCase;
import com.example.auditoria.usecase.RegistrarHallazgoUseCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(HallazgoController.class)
@DisplayName("Pruebas de la capa Web (HallazgoController y RestExceptionHandler)")
class HallazgoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RegistrarHallazgoUseCase registrarUseCase;

    @MockBean
    private IniciarRemediacionUseCase iniciarRemediacionUseCase;

    @MockBean
    private CerrarHallazgoUseCase cerrarUseCase;

    @MockBean
    private ReabrirHallazgoUseCase reabrirUseCase;

    @MockBean
    private ConsultarHallazgoUseCase consultarUseCase;

    @MockBean
    private ConsultarHistorialUseCase consultarHistorialUseCase;

    @MockBean
    private ObtenerDashboardAuditoriaUseCase obtenerDashboardUseCase;

    @Test
    @DisplayName("POST /api/hallazgos retorna 201 Created con hallazgoId")
    void registrarRetorna201() throws Exception {
        UUID idGenerado = UUID.randomUUID();
        when(registrarUseCase.registrar(any())).thenReturn(HallazgoId.de(idGenerado));

        String requestBody = """
                {
                    "titulo": "Falta de conciliación bancaria",
                    "descripcion": "No se realizaron conciliaciones en julio",
                    "area": "Tesorería",
                    "severidad": "ALTA",
                    "fechaDeteccion": "2026-08-01"
                }
                """;

        mockMvc.perform(post("/api/hallazgos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.hallazgoId").value(idGenerado.toString()));
    }

    @Test
    @DisplayName("PATCH /api/hallazgos/{id}/iniciar-remediacion retorna 200 OK")
    void iniciarRemediacionRetorna200() throws Exception {
        UUID id = UUID.randomUUID();
        doNothing().when(iniciarRemediacionUseCase).iniciarRemediacion(eq(HallazgoId.de(id)), any(), any());

        String requestBody = """
                {
                    "responsable": "Laura Gómez",
                    "fechaLimite": "2026-09-30",
                    "notas": "Plan de choque implementado"
                }
                """;

        mockMvc.perform(patch("/api/hallazgos/{id}/iniciar-remediacion", id)
                        .header("X-Usuario", "auditor_lider")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("EN_REMEDIACION"));
    }

    @Test
    @DisplayName("PATCH /api/hallazgos/{id}/cerrar sin remediación previa retorna 400 Bad Request")
    void cerrarSinRemediacionRetorna400() throws Exception {
        UUID id = UUID.randomUUID();
        doThrow(new IllegalStateException("No se puede cerrar un hallazgo sin plan de remediación previo"))
                .when(cerrarUseCase).cerrar(eq(HallazgoId.de(id)), any());

        mockMvc.perform(patch("/api/hallazgos/{id}/cerrar", id))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje").value("No se puede cerrar un hallazgo sin plan de remediación previo"));
    }

    @Test
    @DisplayName("PATCH /api/hallazgos/{id}/cerrar tras remediación retorna 200 OK")
    void cerrarTrasRemediacionRetorna200() throws Exception {
        UUID id = UUID.randomUUID();
        doNothing().when(cerrarUseCase).cerrar(eq(HallazgoId.de(id)), any());

        mockMvc.perform(patch("/api/hallazgos/{id}/cerrar", id)
                        .header("X-Usuario", "auditor_lider"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("CERRADO"));
    }

    @Test
    @DisplayName("PATCH /api/hallazgos/{id}/reabrir un hallazgo cerrado retorna 200 OK")
    void reabrirCerradoRetorna200() throws Exception {
        UUID id = UUID.randomUUID();
        doNothing().when(reabrirUseCase).reabrir(eq(HallazgoId.de(id)), any(), any());

        String requestBody = """
                {
                    "motivo": "Reincidencia observada en la revisión de seguimiento"
                }
                """;

        mockMvc.perform(patch("/api/hallazgos/{id}/reabrir", id)
                        .header("X-Usuario", "comite_auditoria")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("REABIERTO"));
    }

    @Test
    @DisplayName("PATCH /api/hallazgos/{id}/reabrir un hallazgo abierto retorna 400 Bad Request")
    void reabrirAbiertoRetorna400() throws Exception {
        UUID id = UUID.randomUUID();
        doThrow(new TransicionInvalidaException(EstadoHallazgo.ABIERTO, EstadoHallazgo.REABIERTO))
                .when(reabrirUseCase).reabrir(eq(HallazgoId.de(id)), any(), any());

        String requestBody = """
                {
                    "motivo": "Motivo no aplicable"
                }
                """;

        mockMvc.perform(patch("/api/hallazgos/{id}/reabrir", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje").value("Transición de estado inválida: no es posible pasar de ABIERTO a REABIERTO"));
    }

    @Test
    @DisplayName("GET /api/hallazgos lista hallazgos retornando 200 OK")
    void listarRetorna200() throws Exception {
        UUID id = UUID.randomUUID();
        when(consultarUseCase.listarTodos()).thenReturn(List.of(
                new HallazgoResponse(
                        id,
                        "Acceso no revocado",
                        "Ex-empleado aún con credenciales activas",
                        "Seguridad",
                        "CRITICA",
                        LocalDate.of(2026, 8, 1),
                        "ABIERTO",
                        new PlanRemediacionResponse("Admin TI", LocalDate.of(2026, 8, 5), "Desactivar usuario"),
                        null
                )
        ));

        mockMvc.perform(get("/api/hallazgos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(id.toString()))
                .andExpect(jsonPath("$[0].titulo").value("Acceso no revocado"));
    }

    @Test
    @DisplayName("GET /api/hallazgos/dashboard retorna agregaciones 200 OK")
    void obtenerDashboardRetorna200() throws Exception {
        when(obtenerDashboardUseCase.obtenerDashboard()).thenReturn(new DashboardAuditoriaView(
                List.of(new ConteoCategoria("CRITICA", 4)),
                List.of(new ConteoCategoria("CERRADO", 2)),
                List.of(new PromedioCategoria("Finanzas", 14.5))
        ));

        mockMvc.perform(get("/api/hallazgos/dashboard"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.porSeveridad[0].categoria").value("CRITICA"))
                .andExpect(jsonPath("$.porSeveridad[0].total").value(4))
                .andExpect(jsonPath("$.promedioDiasCierrePorArea[0].area").value("Finanzas"))
                .andExpect(jsonPath("$.promedioDiasCierrePorArea[0].promedioDias").value(14.5));
    }

    @Test
    @DisplayName("GET /api/hallazgos/{id}/historial retorna trazabilidad cronológica 200 OK")
    void consultarHistorialRetorna200() throws Exception {
        UUID id = UUID.randomUUID();
        when(consultarHistorialUseCase.listarPorHallazgo(HallazgoId.de(id))).thenReturn(List.of(
                new CambioEstadoView(
                        UUID.randomUUID(), id, "ABIERTO", "EN_REMEDIACION",
                        "auditor1", "Inicio", LocalDateTime.now()
                )
        ));

        mockMvc.perform(get("/api/hallazgos/{id}/historial", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].estadoAnterior").value("ABIERTO"))
                .andExpect(jsonPath("$[0].estadoNuevo").value("EN_REMEDIACION"))
                .andExpect(jsonPath("$[0].usuario").value("auditor1"));
    }
}
