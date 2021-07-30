package com.example.jasper;

import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.parser.PdfTextExtractor;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperReport;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.Month;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.MatcherAssert.*;
import static org.junit.jupiter.api.Assertions.*;

class JasperReportsDemoTest {

    private static final Logger logger = LoggerFactory.getLogger(JasperReportsDemoTest.class);

    @Test
    public void getPdfContent() throws Exception {
        File file = new File(getClass().getClassLoader()
                .getResource("order-info.jrxml").getFile());
        JasperReport jasperReport = null;
        try (InputStream inputStream = new ByteArrayInputStream(Files.readAllBytes(file.toPath()))) {
            jasperReport = JasperCompileManager.compileReport(inputStream);
        } catch (JRException | IOException e) {
            logger.error("pdf generation error ", e);
        }

        Map<String, Object> orderInfo = new HashMap<>();
        orderInfo.put("firstName", "Hamza");
        orderInfo.put("lastName", "Belmellouki");
        orderInfo.put("productName", "Effective Java (3rd Edition)");
        orderInfo.put("price", "45 $");
        orderInfo.put("orderId", "6f64b98a-a0eb-4e75-b436-030258872f43");
        orderInfo.put("orderDate", LocalDate.of(2021, Month.AUGUST, 1).toString());

        byte[] content = JasperReportsDemo.getPdfContent(jasperReport, orderInfo);
        assertNotNull(content);
        PdfTextExtractor parser = new PdfTextExtractor(
                new PdfReader(new ByteArrayInputStream(content)));

        String pdfText = parser.getTextFromPage(1);
        assertThat(pdfText, CoreMatchers.containsString("Hamza"));
        assertThat(pdfText, CoreMatchers.containsString("Belmellouki"));
        assertThat(pdfText, CoreMatchers.containsString("Effective Java (3rd Edition)"));
        assertThat(pdfText, CoreMatchers.containsString("45 $"));
        assertThat(pdfText, CoreMatchers.containsString("6f64b98a-a0eb-4e75-b436-030258872f43"));
        assertThat(pdfText, CoreMatchers.containsString(LocalDate.of(2021, Month.AUGUST, 1).toString()));
    }
}