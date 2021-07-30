package com.example.jasper;

import net.sf.jasperreports.engine.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.Month;
import java.util.HashMap;
import java.util.Map;

public class JasperReportsDemo {

    private static final Logger logger = LoggerFactory.getLogger(JasperReportsDemo.class);

    public static void main(String[] args) throws IOException {
        JasperReportsDemo demo = new JasperReportsDemo();

        Map<String, Object> orderInfo = new HashMap<>();
        orderInfo.put("firstName", "Hamza");
        orderInfo.put("lastName", "Belmellouki");
        orderInfo.put("productName", "Effective Java (3rd Edition)");
        orderInfo.put("price", "45 $");
        orderInfo.put("orderId", "6f64b98a-a0eb-4e75-b436-030258872f43");
        orderInfo.put("orderDate", LocalDate.of(2021, Month.AUGUST, 1).toString());

        demo.generateDocument(orderInfo);
    }

    public void generateDocument(Map<String, Object> parameters) throws IOException {
        File file = new File(getClass().getClassLoader()
                .getResource("order-info.jrxml").getFile());

        JasperReport jasperReport = null;
        try (InputStream inputStream = new ByteArrayInputStream(Files.readAllBytes(file.toPath()))) {
            jasperReport = JasperCompileManager.compileReport(inputStream);
        } catch (JRException | IOException e) {
            logger.error("pdf generation error ", e);
        }

        OutputStream outputStream = new FileOutputStream("order-info.pdf");
        /* Write content to PDF file */
        outputStream.write(getPdfContent(jasperReport, parameters));

    }

    public static byte[] getPdfContent(JasperReport jasperReport, Map<String, Object> parameters) {
        byte[] content = new byte[0];
        JasperPrint jasperPrint;
        try {
            /* Using compiled version(.jasper) of Jasper report to generate PDF */
            jasperPrint = JasperFillManager
                    .fillReport(jasperReport, parameters, new JREmptyDataSource());
            content = JasperExportManager.exportReportToPdf(jasperPrint);
        } catch (JRException e) {
            logger.error("pdf generation error ", e);
        }
        return content;
    }


}
