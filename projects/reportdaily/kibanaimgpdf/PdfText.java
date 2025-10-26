final class PdfText {
  static String tryExtractQuick(Path pdf) throws IOException {
    try (PDDocument doc = Loader.loadPDF(pdf.toFile())) {
      PDFTextStripper stripper = new PDFTextStripper();
      stripper.setSortByPosition(true);
      return stripper.getText(doc);
    }
  }
}
