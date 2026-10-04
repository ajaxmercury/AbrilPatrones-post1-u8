package com.example.auditoria.usecase;

import com.example.auditoria.domain.entity.HallazgoAuditoria;
import com.example.auditoria.domain.valueobject.EstadoHallazgo;
import com.example.auditoria.domain.valueobject.HallazgoId;
import com.example.auditoria.domain.valueobject.PlanRemediacion;
import com.example.auditoria.domain.valueobject.Severidad;
import com.example.auditoria.usecase.impl.CerrarHallazgoService;
import com.example.auditoria.usecase.impl.ConsultarHallazgoService;
import com.example.auditoria.usecase.impl.IniciarRemediacionService;
import com.example.auditoria.usecase.impl.ReabrirHallazgoService;
import com.example.auditoria.usecase.impl.RegistrarHallazgoService;
import com.example.auditoria.usecase.port.HallazgoRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pruebas unitarias de casos de uso (POJOs puros con mock/in-memory port)")
class UseCasesTest {

    private InMemoryHallazgoRepository repository;
    private RegistrarHallazgoService registrarService;
    private IniciarRemediacionService iniciarRemediacionService;
    private CerrarHallazgoService cerrarService;
    private ReabrirHallazgoService reabrirService;
    private ConsultarHallazgoService consultarService;

    static class InMemoryHallazgoRepository implements HallazgoRepositoryPort {
        private final Map<HallazgoId, HallazgoAuditoria> database = new HashMap<>();

        @Override
        public HallazgoAuditoria guardar(HallazgoAuditoria hallazgo) {
            database.put(hallazgo.getId(), hallazgo);
            return hallazgo;
        }

        @Override
        public Optional<HallazgoAuditoria> buscarPorId(HallazgoId id) {
            return Optional.ofNullable(database.get(id));
        }

        @Override
        public List<HallazgoAuditoria> buscarTodos() {
            return new ArrayList<>(database.values());
        }
    }

    @BeforeEach
    void setUp() {
        repository = new InMemoryHallazgoRepository();
        registrarService = new RegistrarHallazgoService(repository);
        iniciarRemediacionService = new IniciarRemediacionService(repository);
        cerrarService = new CerrarHallazgoService(repository);
        reabrirService = new ReabrirHallazgoService(repository);
        consultarService = new ConsultarHallazgoService(repository);
    }

    @Test
    @DisplayName("Registrar hallazgo persiste y retorna UUID válido")
    void registrarHallazgoExitoso() {
        RegistrarHallazgoCommand cmd = new RegistrarHallazgoCommand(
                "Acceso no autorizado",
                "Permisos excesivos en BD",
                "TI",
                Severidad.CRITICA,
                LocalDate.of(2026, 8, 1)
        );

        HallazgoId id = registrarService.registrar(cmd);
        assertNotNull(id);
        assertNotNull(id.valor());

        HallazgoResponse resp = consultarService.buscarPorId(id);
        assertEquals("Acceso no autorizado", resp.titulo());
        assertEquals("ABIERTO", resp.estado());
        assertEquals("TI", resp.area());
    }

    @Test
    @DisplayName("Flujo de remediación, cierre y reapertura a través de casos de uso")
    void flujoCasosDeUso() {
        HallazgoId id = registrarService.registrar(new RegistrarHallazgoCommand(
                "Hallazgo 1", "Desc", "Operaciones", Severidad.MEDIA, LocalDate.now()
        ));

        PlanRemediacion plan = new PlanRemediacion("Laura Gómez", LocalDate.now().plusDays(15), "Plan A");
        iniciarRemediacionService.iniciarRemediacion(id, plan, "auditor1");

        HallazgoResponse remediado = consultarService.buscarPorId(id);
        assertEquals("EN_REMEDIACION", remediado.estado());
        assertEquals("Laura Gómez", remediado.planRemediacion().responsable());

        cerrarService.cerrar(id, "auditor1");
        HallazgoResponse cerrado = consultarService.buscarPorId(id);
        assertEquals("CERRADO", cerrado.estado());
        assertNotNull(cerrado.fechaCierre());

        reabrirService.reabrir(id, "Evidencia insuficiente de remediación", "auditor_jefe");
        HallazgoResponse reabierto = consultarService.buscarPorId(id);
        assertEquals("REABIERTO", reabierto.estado());
        assertNull(reabierto.fechaCierre());
    }

    @Test
    @DisplayName("Buscar hallazgo inexistente lanza HallazgoNotFoundException")
    void buscarInexistenteLanzaNotFound() {
        HallazgoId noExiste = HallazgoId.nuevo();
        assertThrows(HallazgoNotFoundException.class, () -> consultarService.buscarPorId(noExiste));
    }
}
