// com/tuempresa/reports/extract/ImagePdfOcrExtractor.java
package com.tuempresa.reports.extract;

import com.tuempresa.reports.parse.KibanaTextParser;
import com.tuempresa.reports.util.TessdataLoader;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

public class ImagePdfOcrExtractor implements ReportExtractor {

    private final String language; // "eng" o "eng+spa" si quisieras
    private final boolean numbersOnly; // whitelist de dígitos para celdas numéricas (opcional)

    public ImagePdfOcrExtractor() {
        this("eng", false);
    }
    public ImagePdfOcrExtractor(String language, boolean numbersOnly) {
        this.language = language;
        this.numbersOnly = numbersOnly;
    }

    @Override
    public Map<String, Long> extract(File pdf) throws Exception {
        Map<String, Long> result = new LinkedHashMap<>();

        try (PDDocument doc = Loader.loadPDF(pdf)) {
            PDFRenderer renderer = new PDFRenderer(doc);

            // Prepara tessdata desde resources a un directorio temporal
            String[] langs = language.contains("+") ? language.split("\\+") : new String[]{language};
            Path tessdata = TessdataLoader.prepareTessdataToTemp(langs);

            ITesseract tesseract = new Tesseract();
            tesseract.setDatapath(tessdata.toString());
            tesseract.setLanguage(language);
            tesseract.setTessVariable("preserve_interword_spaces", "1");
            if (numbersOnly) {
                tesseract.setTessVariable("tessedit_char_whitelist", "0123456789");
            }

            StringBuilder allPages = new StringBuilder();
            for (int i = 0; i < doc.getNumberOfPages(); i++) {
                // Render 300 DPI para buena precisión
                BufferedImage img = renderer.renderImageWithDPI(i, 300);
                // (Opcional) pre-proceso leve: guardar como PNG sin compresión ayuda a OCR
                // ImageIO.write(img, "png", new File("page-" + i + ".png"));
                String pageText = tesseract.doOCR(img);
                allPages.append(pageText).append("\n");
            }

            result.putAll(KibanaTextParser.parseTitleCountBlocks(allPages.toString()));
        }
        return result;
    }
}
