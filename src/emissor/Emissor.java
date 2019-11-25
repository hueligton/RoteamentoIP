/*
  @author Felipe Alves Matos Caggi
 * @author Hueligton Pereira de Melo
 * Trabalho 2 - Roteamento IP
 * Professora: Hana Karina S. Rubinsztejn
 */
package emissor;

import pacote.BasePacket;
import pacote.Data;
import pacote.IPv4;
import pacote.UDP;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Emissor {

    public static void main(String[] args) throws Exception {
        DatagramSocket clientSocket = new DatagramSocket();
        InetAddress IPAddress = InetAddress.getByName(args[0]);
        int port = Integer.parseInt(args[1]);

        BasePacket iPv4 = new IPv4()
                .setTtl((byte) 5)
                .setSourceAddress(args[2])
                .setDestinationAddress(args[3]);

        iPv4.setPayload(new UDP()
                .setSourcePort((short) 80)
                .setDestinationPort((short) port)
                .setPayload(new Data(args[4].getBytes())));
        byte[] packet = iPv4.serialize();

        DatagramPacket sendPacket = new DatagramPacket(packet, packet.length, IPAddress, port);
        clientSocket.send(sendPacket);
        clientSocket.close();
    }
}
