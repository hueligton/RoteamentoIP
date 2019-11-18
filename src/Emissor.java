/*
  @author Felipe Alves Matos Caggi
 * @author Hueligton Pereira de Melo
 * Trabalho 2 - Roteamento IP
 * Professora: Hana Karina S. Rubinsztejn
 */

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

class Emissor {

    public static void main(String[] args) throws Exception {
        DatagramSocket clientSocket = new DatagramSocket();
        InetAddress IPAddress = InetAddress.getByName(args[0]);
        int port = Integer.parseInt(args[1]);

        Pacote pacote = new Pacote(args[2], args[3], args[4]);

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutput out;
        byte[] sendData;

        try {
            out = new ObjectOutputStream(bos);
            out.writeObject(pacote);
            out.flush();
            sendData = bos.toByteArray();
        } finally {
            try {
                bos.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
        clientSocket.send(sendPacket);
        clientSocket.close();
    }
}
