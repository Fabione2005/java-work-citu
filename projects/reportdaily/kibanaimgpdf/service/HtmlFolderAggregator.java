// HtmlFolderAggregator.java
package com.citi.reports.service;

import java.io.File;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class HtmlFolderAggregator {

    private final HtmlReportParserService parser;
    private final boolean strictOrder; // si true, falla si otro HTML cambia el orden/títulos

    public HtmlFolderAggregator(HtmlReportParserService parser) {
        this(parser, false);
    }

    public HtmlFolderAggregator(HtmlReportParserService parser, boolean strictOrder) {
        this.parser = parser;
        this.strictOrder = strictOrder;
    }

    public Map<String, Long> aggregateFolder(Path folder) throws Exception {
        if (!Files.isDirectory(folder)) {
            throw new IllegalArgumentException("Not a folder: " + folder);
        }

        // 1) Recolectar HTMLs y ordenarlos por nombre (reproducible)
        List<Path> files;
        try (Stream<Path> s = Files.list(folder)) {
            files = s.filter(p -> p.toString().toLowerCase().endsWith(".html"))
                     .sorted(Comparator.comparing(p -> p.getFileName().toString().toLowerCase()))
                     .collect(Collectors.toList());
        }
        if (files.isEmpty()) return Collections.emptyMap();

        // 2) Sembrar orden con el primer HTML
        Map<String, Long> totals = new LinkedHashMap<>();
        Map<String, Long> firstMap = parser.parseKibanaHtmlAsMap(files.get(0).toFile());
        firstMap.forEach(totals::put); // mismo orden que en el primer HTML
        List<String> firstOrder = new ArrayList<>(firstMap.keySet());

        // 3) Agregar el resto sumando valores (sin cambiar el orden sembrado)
        for (int i = 1; i < files.size(); i++) {
            Map<String, Long> map = parser.parseKibanaHtmlAsMap(files.get(i).toFile());

            // Validación opcional: mismos títulos y mismo orden
            if (strictOrder) {
                List<String> currentOrder = new ArrayList<>(map.keySet());
                if (!currentOrder.equals(firstOrder)) {
                    throw new IllegalStateException("HTML order/titles differ in: "
                            + files.get(i).getFileName());
                }
            }

            // Sumar: títulos nuevos (si los hubiera) se agregan al final
            for (Map.Entry<String, Long> e : map.entrySet()) {
                totals.merge(e.getKey(), e.getValue(), Long::sum);
            }
        }
        return totals;
    }
}
