// AppDemo.java
import com.tuempresa.reports.service.PdfReportService;
import java.io.File;
import java.util.Map;

public class AppDemo {
    public static void main(String[] args) throws Exception {
        File pdf = new File("C:/ruta/a/tu/reporte.pdf"); // Kibana o Splunk
        PdfReportService svc = new PdfReportService();
        Map<String, Long> metrics = svc.extractMetrics(pdf);
        metrics.forEach((k, v) -> System.out.println(k + " => " + v));
        // Aquí pasas "metrics" a tu generador de Excel existente sin tocarlo.
    }
}
