package com.citi.reports;

import com.citi.reports.model.KibanaMetric;
import com.citi.reports.service.HtmlReportParserService;
import com.citi.reports.util.FileUtils;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;
import java.util.List;

@SpringBootApplication
public class ReportApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(ReportApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        HtmlReportParserService parser = new HtmlReportParserService();

        // Ruta local al HTML exportado desde Kibana
        File html = FileUtils.validateLocalFile("C:/Users/<tu_usuario>/Downloads/170182_Error_Reporting.html");

        List<KibanaMetric> metrics = parser.parseKibanaHtml(html);

        System.out.println("✅ Metrics extracted from Kibana HTML:\n");
        metrics.forEach(System.out::println);
    }
}
