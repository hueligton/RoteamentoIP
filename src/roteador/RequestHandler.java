/*
  @author Felipe Alves Matos Caggi
 * @author Hueligton Pereira de Melo
 * Trabalho 2 - Roteamento IP
 * Professora: Hana Karina S. Rubinsztejn
 */
package roteador;

import pacote.IPv4;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Comparator;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Stream;

class RequestHandler implements Runnable {
    private final DatagramSocket serverSocket;
    private DatagramPacket receivePacket;
    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    RequestHandler(DatagramSocket serverSocket, DatagramPacket receivePacket) {
        this.serverSocket = serverSocket;
        this.receivePacket = receivePacket;
    }

    @Override
    public void run() {
        try {
            byte[] data = receivePacket.getData();
            IPv4 pacote = new IPv4();
            pacote.deserialize(data, 0, data.length);

            pacote.setTtl((byte) (pacote.getTtl() - 1));

            if (pacote.getTtl() > 0) {
                Stream<LinhaRoteamento> linhaRoteamentoStream = Roteador.tabelaRoteamento.getTabela().stream().filter(linhaRoteamento -> {
                    String networkAddress = new SubnetUtils(pacote.getDestinationAddress().toString(), linhaRoteamento.getMascara()).getInfo().getNetworkAddress();
                    return networkAddress.equals(linhaRoteamento.getRedeDestino());
                });

                Optional<LinhaRoteamento> longestMatch = linhaRoteamentoStream.max(Comparator.comparing(LinhaRoteamento::getCidrNotation));

                String gateway;

                if (longestMatch.isEmpty()) {
                    addLogInfo(String.format("destino %s não encontrado na tabela de roteamento, descartando pacote", pacote.getDestinationAddress()));
                } else {
                    gateway = longestMatch.get().getGateway();

                    if (gateway.equals("0.0.0.0")) {
                        String dados = new String(pacote.getPayload());
                        addLogInfo(String.format("destino alcançado. De %s para %s : %s", pacote.getSourceAddress(), pacote.getDestinationAddress(), dados));
                    } else {
                        InetAddress IPAddress = InetAddress.getByName(gateway);
                        int port = longestMatch.get().getPortaInterface();

                        addLogInfo(String.format("pacote de encaminhamento para %s para o próximo salto %s sobre a interface %s", pacote.getDestinationAddress(), gateway, port));

                        byte[] sendData = pacote.serialize();

                        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
                        serverSocket.send(sendPacket);
                    }
                }

            } else {
                addLogInfo(String.format("tempo de vida excedido em trânsito, descartando o pacote para %s", pacote.getDestinationAddress()));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addLogInfo(String message) {
        LOGGER.info(message);
    }
}
