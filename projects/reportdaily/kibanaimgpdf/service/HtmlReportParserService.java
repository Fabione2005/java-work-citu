package com.citi.reports.service;

import com.citi.reports.model.KibanaMetric;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class HtmlReportParserService {

    public List<KibanaMetric> parseKibanaHtml(File htmlFile) throws Exception {
        List<KibanaMetric> metrics = new ArrayList<>();

        Document doc = Jsoup.parse(htmlFile, StandardCharsets.UTF_8.name());

        // 1️⃣ Obtener todos los divs que representan cada "panel"
        Elements panels = doc.select("div.react-grid-item.react-draggable.react-resizable");

        for (Element panel : panels) {
            try {
                // 2️⃣ Buscar el título del panel
                Element titleEl = panel.selectFirst("h2.embPanel__title span.embPanel__titleText");
                String title = (titleEl != null) ? titleEl.text().trim() : "Unknown";

                // 3️⃣ Buscar el valor numérico dentro del panel
                Element valueEl = panel.selectFirst("div.mtrVis__value span[ng-non-bindable]");
                Long value = 0L;
                if (valueEl != null) {
                    String numberStr = valueEl.text().replaceAll("[^0-9]", "");
                    if (!numberStr.isEmpty()) {
                        value = Long.parseLong(numberStr);
                    }
                }

                // 4️⃣ Agregar el resultado
                metrics.add(new KibanaMetric(title, value));
            } catch (Exception e) {
                System.err.println("Error parsing one panel: " + e.getMessage());
            }
        }

        return metrics;
    }

    // com.citi.reports.service.HtmlReportParserService
public Map<String, Long> parseKibanaHtmlAsMap(File htmlFile) throws Exception {
    Map<String, Long> results = new LinkedHashMap<>();

    Document doc = Jsoup.parse(htmlFile, StandardCharsets.UTF_8.name());
    Elements panels = doc.select("div.react-grid-item.react-draggable.react-resizable");

    for (Element panel : panels) {
        Element titleEl = panel.selectFirst("h2.embPanel__title span.embPanel__titleText");
        String title = (titleEl != null) ? titleEl.text().trim() : "Unknown";

        Element valueEl = panel.selectFirst("div.mtrVis__value span[ng-non-bindable]");
        long value = 0L;
        if (valueEl != null) {
            String numberStr = valueEl.text().replaceAll("[^0-9]", "");
            if (!numberStr.isEmpty()) value = Long.parseLong(numberStr);
        }

        // Si un mismo título aparece más de una vez en el MISMO HTML, también sumamos
        results.merge(title, value, Long::sum);
    }
    return results;
}
}
