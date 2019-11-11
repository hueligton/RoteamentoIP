import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Optional;
import java.util.logging.Logger;

public class RequestHandler implements Runnable {
    private final DatagramSocket serverSocket;
    private DatagramPacket receivePacket;
    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    public RequestHandler(DatagramSocket serverSocket, DatagramPacket receivePacket) {
        this.serverSocket = serverSocket;
        this.receivePacket = receivePacket;
    }

    @Override
    public void run() {
        try {
            String sentence = new String(receivePacket.getData());
            String[] sentenceSplit = sentence.split("/");

            Optional<LinhaRoteamento> first = Receptor.tabelaRoteamento.getTabela().stream().filter(linhaRoteamento -> {
                String networkAddress = new SubnetUtils(sentenceSplit[1], linhaRoteamento.getMascara()).getInfo().getNetworkAddress();
                return networkAddress.equals(linhaRoteamento.getRedeDestino());
            }).findFirst();

            String gateway = first.get().getGateway();
            if (gateway.equals("0.0.0.0")) {
                addLogInfo("Entregue " + sentenceSplit[2]);
            } else {
                InetAddress IPAddress = InetAddress.getByName(gateway);
                int port = first.get().getPortaInterface();

                addLogInfo(gateway + " " + port + " " + sentenceSplit[2]);

                byte[] sendData = sentence.getBytes();
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
                serverSocket.send(sendPacket);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void addLogInfo(String message) {
        LOGGER.info(message);
    }
}
