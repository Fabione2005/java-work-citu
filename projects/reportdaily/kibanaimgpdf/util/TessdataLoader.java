// com/tuempresa/reports/util/TessdataLoader.java
package com.tuempresa.reports.util;

import java.io.InputStream;
import java.nio.file.*;
import java.util.Objects;

public final class TessdataLoader {
    private TessdataLoader() {}

    /**
     * Copia los archivos .traineddata desde resources (/tessdata/...) a un directorio temporal
     * y devuelve la ruta de esa carpeta.
     */
    public static Path prepareTessdataToTemp(String... languages) throws Exception {
        Path temp = Files.createTempDirectory("tessdata");
        for (String lang : languages) {
            String resPath = "/tessdata/" + lang + ".traineddata";
            try (InputStream in = TessdataLoader.class.getResourceAsStream(resPath)) {
                Objects.requireNonNull(in, "No se encontró " + resPath + " en resources");
                Files.copy(in, temp.resolve(lang + ".traineddata"), StandardCopyOption.REPLACE_EXISTING);
            }
        }
        return temp;
    }
}
