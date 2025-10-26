
package com.tuempresa.reports.extract;

import java.io.File;
import java.util.Map;

public interface ReportExtractor {
    Map<String, Long> extract(File pdf) throws Exception;
}
