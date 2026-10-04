package com.example.auditoria.architecture;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Verificación de Regla de Dependencia y Pureza Arquitectónica (Clean Architecture)")
class PurezaArquitectonicaTest {

    private static final List<String> IMPORTS_PROHIBIDOS = List.of(
            "org.springframework",
            "jakarta.persistence",
            "javax.persistence",
            "org.hibernate"
    );

    @Test
    @DisplayName("domain/ y usecase/ no deben importar Spring, JPA ni frameworks externos")
    void validarPurezaDominioYCasosDeUso() throws IOException {
        Path basePath = Paths.get("src", "main", "java", "com", "example", "auditoria");
        Path domainPath = basePath.resolve("domain");
        Path usecasePath = basePath.resolve("usecase");

        List<String> violaciones = new ArrayList<>();

        if (Files.exists(domainPath)) {
            escanearDirectorio(domainPath, violaciones);
        }
        if (Files.exists(usecasePath)) {
            escanearDirectorio(usecasePath, violaciones);
        }

        assertTrue(violaciones.isEmpty(),
                "Se encontraron violaciones a la regla de dependencia de Clean Architecture:\n" +
                        String.join("\n", violaciones));
    }

    private void escanearDirectorio(Path dir, List<String> violaciones) throws IOException {
        try (Stream<Path> stream = Files.walk(dir)) {
            stream.filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(".java"))
                    .forEach(file -> verificarArchivo(file, violaciones));
        }
    }

    private void verificarArchivo(Path file, List<String> violaciones) {
        try {
            List<String> lineas = Files.readAllLines(file);
            for (int i = 0; i < lineas.size(); i++) {
                String linea = lineas.get(i).trim();
                if (linea.startsWith("import ")) {
                    for (String prohibido : IMPORTS_PROHIBIDOS) {
                        if (linea.contains(prohibido)) {
                            violaciones.add(String.format("Archivo %s (línea %d): import prohibido '%s'",
                                    file.toAbsolutePath(), i + 1, linea));
                        }
                    }
                }
            }
        } catch (IOException e) {
            violaciones.add("Error al leer archivo: " + file + " - " + e.getMessage());
        }
    }
}
