/*
  @author Felipe Alves Matos Caggi
 * @author Hueligton Pereira de Melo
 * Trabalho 2 - Roteamento IP
 * Professora: Hana Karina S. Rubinsztejn
 */

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Comparator;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Stream;

public class RequestHandler implements Runnable {
    private final DatagramSocket serverSocket;
    private DatagramPacket receivePacket;
    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    RequestHandler(DatagramSocket serverSocket, DatagramPacket receivePacket) {
        this.serverSocket = serverSocket;
        this.receivePacket = receivePacket;
    }

    @Override
    public void run() {
        ByteArrayInputStream bis;
        ObjectInput in;

        try {
            bis = new ByteArrayInputStream(receivePacket.getData());
            in = new ObjectInputStream(bis);
            Pacote pacote = (Pacote) in.readObject();
            in.close();

            pacote.decrementarTtl();

            if (pacote.getTtl() > 0) {
                Stream<LinhaRoteamento> linhaRoteamentoStream = Receptor.tabelaRoteamento.getTabela().stream().filter(linhaRoteamento -> {
                    String networkAddress = new SubnetUtils(pacote.getEnderecoDestino(), linhaRoteamento.getMascara()).getInfo().getNetworkAddress();
                    return networkAddress.equals(linhaRoteamento.getRedeDestino());
                });

                Optional<LinhaRoteamento> longestMatch = linhaRoteamentoStream.sorted(Comparator.comparing(LinhaRoteamento::getCidrNotation).reversed()).findFirst();

                String gateway;

                if (longestMatch.isEmpty()) {
                    addLogInfo(String.format("destino %s não encontrado na tabela de roteamento, descartando pacote", pacote.getEnderecoDestino()));
                } else {
                    gateway = longestMatch.get().getGateway();

                    if (gateway.equals("0.0.0.0")) {
                        addLogInfo(String.format("destino alcançado. De %s para %s : %s", pacote.getEnderecoOrigem(), pacote.getEnderecoDestino(), pacote.getConteudo()));
                    } else {
                        InetAddress IPAddress = InetAddress.getByName(gateway);
                        int port = longestMatch.get().getPortaInterface();

                        addLogInfo(String.format("pacote de encaminhamento para %s para o próximo salto %s sobre a interface %s", pacote.getEnderecoDestino(), gateway, port));

                        ByteArrayOutputStream bos = new ByteArrayOutputStream();
                        ObjectOutput out;
                        byte[] sendData;

                        try {
                            out = new ObjectOutputStream(bos);
                            out.writeObject(pacote);
                            out.flush();
                            sendData = bos.toByteArray();
                        } finally {
                            bos.close();
                        }

                        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
                        serverSocket.send(sendPacket);
                    }
                }

            } else {
                addLogInfo(String.format("tempo de vida excedido em trânsito, descartando o pacote para %s", pacote.getEnderecoDestino()));
            }

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void addLogInfo(String message) {
        LOGGER.info(message);
    }
}
