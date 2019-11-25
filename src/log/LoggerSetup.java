package log;/*
  @author Felipe Alves Matos Caggi
 * @author Hueligton Pereira de Melo
 * Trabalho 2 - Roteamento IP
 * Professora: Hana Karina S. Rubinsztejn
 */

import java.io.IOException;
import java.util.Date;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class LoggerSetup {

    private static final String LOGFILE_NAME = "access.log";

    public static void setup() {
//        System.setProperty("java.util.logging.SimpleFormatter.format",
//                "[" + new Date().toString() + "] " + "%5$s%n");
        System.setProperty("java.util.logging.SimpleFormatter.format", "%5$s%n");

        Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
        logger.setLevel(Level.ALL);

        FileHandler fileHandler;
        try {
            fileHandler = new FileHandler(LOGFILE_NAME, true);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao criar arquivo de log");
        }

        SimpleFormatter formatter = new SimpleFormatter();
        fileHandler.setFormatter(formatter);
        logger.addHandler(fileHandler);
    }
}
