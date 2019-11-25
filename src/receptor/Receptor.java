/*
  @author Felipe Alves Matos Caggi
 * @author Hueligton Pereira de Melo
 * Trabalho 2 - Roteamento IP
 * Professora: Hana Karina S. Rubinsztejn
 */
package receptor;

import log.LoggerSetup;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Scanner;

public class Receptor implements Runnable {

    private DatagramSocket serverSocket;
    private volatile boolean running = true;
    private static ArrayList<Thread> servicingThreads;
    static TabelaRoteamento tabelaRoteamento;

    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        int port = Integer.parseInt(args[0]);

        carregarTabelaRoteamento(args);

        LoggerSetup.setup();
        Receptor myProxy = new Receptor(port);
        myProxy.listen();
    }

    private static void carregarTabelaRoteamento(String[] args) {
        tabelaRoteamento = new TabelaRoteamento();
        String[] linha;
        for (int i = 1; i < args.length; i++) {
            linha = args[i].split("/");
            if (linha.length < 4) {
                System.out.println("Entrada: " + args[i] + " inválida.");
                System.exit(0);
            }
            tabelaRoteamento.inserirLinhaRoteamento(linha[0], linha[1], linha[2], linha[3]);
        }
    }

    /**
     * Inicializa servidor com a porta informada e carrega dados da sessão anterior
     *
     * @param port
     */
    private Receptor(int port) {
        servicingThreads = new ArrayList<>();
        new Thread(this).start();

        try {
            serverSocket = new DatagramSocket(port);
            System.out.println("Aguardando cliente na porta " + serverSocket.getLocalPort() + "..");
            running = true;
        } catch (SocketException se) {
            System.out.println("Socket Exception durante conexão do cliente");
            se.printStackTrace();
        }
    }

    /**
     * Escuta a porta informada e aceita novas conexões no socket.
     * Cria uma nova thread para lidar com a requisição, encaminha a conexão socket e continua ouvindo
     */
    private void listen() {
        while (running) {
            try {
                byte[] receiveData = new byte[1024];

                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                serverSocket.receive(receivePacket);

                Thread thread = new Thread(new RequestHandler(serverSocket, receivePacket));
                servicingThreads.add(thread);
                thread.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Realiza join de todas as thread de receptor.RequestHandler em execução.
     */
    private void closeServer() {
        System.out.println("\nFechando servidor..");
        running = false;

        try {
            // Fecha todas as threads em execução
            for (Thread thread : servicingThreads) {
                if (thread.isAlive()) {
                    System.out.print("Aguardando " + thread.getId() + " ser fechado..");
                    thread.join();
                    System.out.println(" fechado");
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try {
            System.out.println("Fechando conexão");
            serverSocket.close();
        } catch (Exception e) {
            System.out.println("Exceção ao fechar server socket");
            e.printStackTrace();
        }
    }

    /**
     * Permite visualizar situação atual do servidor
     * close: fecha conexão
     */
    @Override
    public void run() {
        Scanner scanner = new Scanner(System.in);

        String command;
        while (running) {
            System.out.println("Digite \"close\" para fechar o servidor.");
            command = scanner.nextLine();
            if (command.equals("close")) {
                running = false;
                closeServer();
            }
        }
        scanner.close();
    }
}
