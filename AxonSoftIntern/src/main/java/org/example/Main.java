package org.example;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
    /*
    * Buna ziua! Am rezolvat cerinta data de catre dumneavoastra folosind sl4j si logback pentru logger
    * si OpenCsv pentru citirea fisierului,am scris comentariile in romana, sper sa fie bine si sa nu va deranjeze,
    * promit ca stiu engleza =).
    */
public class Main {
    public static void main(String[] args) {
        final Logger logger = LoggerFactory.getLogger(Main.class);
        ApplicantsProcessor processor = new ApplicantsProcessor();

        try (InputStream inputStream = new FileInputStream("src/main/resources/example1.csv")) {
            logger.info("Reading file 1");
            String resultJson = processor.processAppliants(inputStream);
            System.out.println(resultJson);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        System.out.println();
        try (InputStream inputStream = new FileInputStream("src/main/resources/example2.csv")) {
            logger.info("Reading file 2");
            String resultJson = processor.processAppliants(inputStream);
            System.out.println(resultJson);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }
}
