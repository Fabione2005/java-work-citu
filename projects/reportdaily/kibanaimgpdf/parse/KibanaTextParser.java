// com/tuempresa/reports/parse/KibanaTextParser.java
package com.tuempresa.reports.parse;

import java.util.*;
import java.util.regex.*;

public final class KibanaTextParser {
    private KibanaTextParser() {}

    /**
     * Busca bloques en formato:
     *   Título
     *   1,234
     *   Count
     */
    public static Map<String, Long> parseTitleCountBlocks(String rawText) {
        Map<String, Long> map = new LinkedHashMap<>();

        // Normalización: espacios duros y separador de miles
        String norm = rawText.replace('\u00A0', ' ').replace(",", "");

        Pattern p = Pattern.compile("(?m)^(?<title>.+?)\\s*\\R\\s*(?<num>\\d{1,12})\\s*\\R\\s*Count\\b");
        Matcher m = p.matcher(norm);

        while (m.find()) {
            String title = m.group("title").trim();
            long val = Long.parseLong(m.group("num").trim());
            map.put(normalizeTitle(title), val);
        }
        return map;
    }

    private static String normalizeTitle(String t) {
        // Corrige confusiones frecuentes de OCR (I/l, 0/O) si hiciera falta
        String s = t.replace("Invalidlnput", "InvalidInput"); // l -> I
        return s;
    }
}
