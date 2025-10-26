// com/tuempresa/reports/service/PdfReportService.java
package com.tuempresa.reports.service;

import com.tuempresa.reports.extract.ImagePdfOcrExtractor;
import com.tuempresa.reports.extract.ReportExtractor;
import com.tuempresa.reports.extract.TextPdfExtractor;
import com.tuempresa.reports.util.PdfText;

import java.io.File;
import java.util.Map;

public class PdfReportService {

    private final ReportExtractor textExtractor = new TextPdfExtractor();
    private final ReportExtractor ocrExtractor  = new ImagePdfOcrExtractor("eng", false);

    /**
     * Intenta extraer texto directo; si no hay, usa OCR.
     */
    public Map<String, Long> extractMetrics(File pdf) throws Exception {
        String quick = "";
        try {
            quick = PdfText.tryExtractQuick(pdf);
        } catch (Exception ignored) { /* si falla, caemos a OCR */ }

        boolean looksText = quick != null && quick.trim().length() > 50;
        if (looksText) {
            Map<String, Long> m = textExtractor.extract(pdf);
            // Si por alguna razón no encontró bloques válidos, cae a OCR
            if (m != null && !m.isEmpty()) return m;
        }
        return ocrExtractor.extract(pdf);
    }
}
