// com.citi.reports.service.HtmlFolderAggregator.java
package com.citi.reports.service;

import java.io.File;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Stream;

public class HtmlFolderAggregator {

    private final HtmlReportParserService parser;

    public HtmlFolderAggregator(HtmlReportParserService parser) {
        this.parser = parser;
    }

    public Map<String, Long> aggregateFolder(Path folder) throws Exception {
        if (!Files.isDirectory(folder)) {
            throw new IllegalArgumentException("La ruta no es carpeta: " + folder);
        }

        Map<String, Long> totals = new TreeMap<>(); // ordenado por título

        try (Stream<Path> paths = Files.list(folder)) {
            paths.filter(p -> p.toString().toLowerCase().endsWith(".html"))
                 .forEach(p -> {
                     try {
                         Map<String, Long> fileMap = parser.parseKibanaHtmlAsMap(p.toFile());
                         fileMap.forEach((k, v) -> totals.merge(k, v, Long::sum));
                     } catch (Exception e) {
                         System.err.println("Error leyendo " + p + ": " + e.getMessage());
                     }
                 });
        }
        return totals;
    }
}
