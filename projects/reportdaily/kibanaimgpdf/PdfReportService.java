public class PdfReportService {

  private final ReportExtractor textExtractor = new TextPdfExtractor();
  private final ReportExtractor ocrExtractor  = new ImagePdfOcrExtractor();

  public Map<String, Long> extractMetrics(Path pdfPath) throws Exception {
    String quickText = PdfText.tryExtractQuick(pdfPath); // PDFBox
    if (quickText != null && quickText.trim().length() > 50) {
      return textExtractor.extract(pdfPath.toFile());
    }
    return ocrExtractor.extract(pdfPath.toFile());
  }
}
