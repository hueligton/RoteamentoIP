import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.PrintWriter;

import static org.junit.jupiter.api.Assertions.*;

class EmissorTest {

    @Test
    void receptorDireto() throws IOException {

        String[] parametros = new String[] {"127.0.0.1", "1111", "1.1.1.1", "10.0.0.5", "Cheguei_R1?!"};

        String[] resposta = new String[]{"destino alcançado. De 1.1.1.1 para 10.0.0.5 : Cheguei_R1?!"};
        String logFilePath = "access.log";

        emitir(parametros, resposta, logFilePath);
    }

    @Test
    void receptorIndiretoR1paraR2() throws IOException {

        String[] parametros = new String[] {"127.0.0.1", "1111", "1.1.1.1", "30.1.2.10",  "Cheguei_R2!?"};

        String[] resposta1 = new String[]{"pacote de encaminhamento para 30.1.2.10 para o próximo salto 127.0.0.1 sobre a interface 2222"};
        String logFilePath1 = "access.log";

        String[] resposta2 = new String[]{"pacote de encaminhamento para 30.1.2.10 para o próximo salto 127.0.0.1 sobre a interface 3333"};
        String logFilePath2 = "access.log.1";

        emitir(parametros, resposta1, logFilePath1);
        emitir(parametros, resposta2, logFilePath2);
    }

    @Test
    void receptorIndiretoR2paraR3() throws IOException {

        String[] parametros = new String[] {"127.0.0.1", "2222", "1.1.1.1", "20.0.2.1", "Repassado_a_4444?!"};

        String[] resposta1 = new String[]{"pacote de encaminhamento para 20.0.2.1 para o próximo salto 127.0.0.1 sobre a interface 3333"};
        String logFilePath1 = "access.log.1";

        String[] resposta2 = new String[]{"pacote de encaminhamento para 20.0.2.1 para o próximo salto 127.0.0.1 sobre a interface 4444"};
        String logFilePath2 = "access.log.2";

        emitir(parametros, resposta1, logFilePath1);
        emitir(parametros, resposta2, logFilePath2);
    }

    @Test
    void receptorInexistenteR1Descarte() throws IOException {
        String[] parametros = new String[] {"127.0.0.1", "1111", "2.2.2.2", "40.0.40.1", "Descartado!!!"};

        String[] resposta1 = new String[]{"destino 40.0.40.1 não encontrado na tabela de roteamento, descartando pacote"};
        String logFilePath1 = "access.log";

        emitir(parametros, resposta1, logFilePath1);
    }

    @Test
    void ttlExecidoR3paraR2paraR3() throws IOException {
        String[] parametros = new String[]{"127.0.0.1", "3333", "2.2.2.2", "10.10.10.10", "TTL_excedido!!!"};

        String[] resposta1 = new String[]{
                "pacote de encaminhamento para 10.10.10.10 para o próximo salto 127.0.0.1 sobre a interface 3333",
                "pacote de encaminhamento para 10.10.10.10 para o próximo salto 127.0.0.1 sobre a interface 3333"
        };
        String logFilePath1 = "access.log.1";

        String[] resposta2 = new String[]{"" +
                "pacote de encaminhamento para 10.10.10.10 para o próximo salto 127.0.0.1 sobre a interface 2222",
                "pacote de encaminhamento para 10.10.10.10 para o próximo salto 127.0.0.1 sobre a interface 2222",
                "tempo de vida excedido em trânsito, descartando o pacote para 10.10.10.10"
        };
        String logFilePath2 = "access.log.2";

        emitir(parametros, resposta1, logFilePath1);
        emitir(parametros, resposta2, logFilePath2);
    }


    private void emitir(String[] parametros, String[] resposta, String logFilePath) throws IOException {
        PrintWriter writer = new PrintWriter(logFilePath);
        writer.print("");
        writer.close();

        try {
            Emissor.main(parametros);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            sleep();
            checarLog(logFilePath, resposta);
        }
    }

    private void sleep() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void checarLog(String filePath, String[] resultadoEsperado) throws IOException {
        FileArrayProvider fap = new FileArrayProvider();
        String[] lines = fap.readLines(filePath);

        for (String line: lines) {
            System.out.println(line);
        }

        assertArrayEquals(lines, resultadoEsperado);
    }
}