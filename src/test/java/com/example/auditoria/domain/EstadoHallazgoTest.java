package com.example.auditoria.domain;

import com.example.auditoria.domain.valueobject.EstadoHallazgo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pruebas unitarias de EstadoHallazgo (Máquina de estados finitos)")
class EstadoHallazgoTest {

    @ParameterizedTest(name = "De {0} a {1} debe ser permitido = {2}")
    @CsvSource({
            "ABIERTO, EN_REMEDIACION, true",
            "ABIERTO, CERRADO, false",
            "ABIERTO, REABIERTO, false",
            "ABIERTO, ABIERTO, false",
            "EN_REMEDIACION, CERRADO, true",
            "EN_REMEDIACION, ABIERTO, false",
            "EN_REMEDIACION, REABIERTO, false",
            "EN_REMEDIACION, EN_REMEDIACION, false",
            "CERRADO, REABIERTO, true",
            "CERRADO, ABIERTO, false",
            "CERRADO, EN_REMEDIACION, false",
            "CERRADO, CERRADO, false",
            "REABIERTO, EN_REMEDIACION, true",
            "REABIERTO, CERRADO, false",
            "REABIERTO, ABIERTO, false",
            "REABIERTO, REABIERTO, false"
    })
    void validarMatrizDeTransiciones(EstadoHallazgo actual, EstadoHallazgo objetivo, boolean esperado) {
        assertEquals(esperado, actual.puedeTransicionarA(objetivo));
    }

    @Test
    @DisplayName("Transición hacia null debe retornar false")
    void transicionANullDebeRetornarFalse() {
        assertFalse(EstadoHallazgo.ABIERTO.puedeTransicionarA(null));
        assertFalse(EstadoHallazgo.EN_REMEDIACION.puedeTransicionarA(null));
        assertFalse(EstadoHallazgo.CERRADO.puedeTransicionarA(null));
        assertFalse(EstadoHallazgo.REABIERTO.puedeTransicionarA(null));
    }
}
