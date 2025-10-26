public class ImagePdfOcrExtractor implements ReportExtractor {

  @Override
  public Map<String, Long> extract(File pdf) throws Exception {
    Map<String, Long> result = new LinkedHashMap<>();

    try (PDDocument doc = Loader.loadPDF(pdf)) {
      PDFRenderer renderer = new PDFRenderer(doc);
      ITesseract tesseract = new Tesseract();
      // <-- Ajusta esta ruta si es necesario
      // tesseract.setDatapath("C:/Program Files/Tesseract-OCR/tessdata");
      tesseract.setLanguage("eng");
      // Mejora de precisión en números/títulos
      tesseract.setTessVariable("preserve_interword_spaces", "1");

      for (int i = 0; i < doc.getNumberOfPages(); i++) {
        BufferedImage img = renderer.renderImageWithDPI(i, 300); // alta resolución
        String pageText = tesseract.doOCR(img);

        // Normalizamos para buscar bloques "Título \n número \n Count"
        result.putAll(parseKibanaCards(pageText));
      }
    }
    return result;
  }

  private Map<String, Long> parseKibanaCards(String text) {
    Map<String, Long> map = new LinkedHashMap<>();
    // Quita separadores de miles tipo "1,511"
    String norm = text.replaceAll("\\u00A0", " ").replace(",", "");

    // Regex: título en una línea, número en la siguiente, luego la palabra Count
    Pattern p = Pattern.compile(
      "(?m)^(?<title>.+?)\\s*\\R\\s*(?<num>\\d{1,9})\\s*\\R\\s*Count\\b");
    Matcher m = p.matcher(norm);
    while (m.find()) {
      String title = m.group("title").trim();
      long val = Long.parseLong(m.group("num"));
      map.put(title, val);
    }
    return map;
  }
}
