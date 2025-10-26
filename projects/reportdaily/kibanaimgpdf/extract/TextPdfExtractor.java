
package com.tuempresa.reports.extract;

import com.tuempresa.reports.parse.KibanaTextParser;
import com.tuempresa.reports.util.PdfText;
import java.io.File;
import java.util.Map;

public class TextPdfExtractor implements ReportExtractor {
    @Override
    public Map<String, Long> extract(File pdf) throws Exception {
        String text = PdfText.tryExtractQuick(pdf);
        return KibanaTextParser.parseTitleCountBlocks(text);
    }
}
