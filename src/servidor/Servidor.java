package servidor;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.*;

// Classe auxiliar para armazenar dados do cliente
class ClientInfo {
    private final InetAddress address;
    private final int port;

    public ClientInfo(InetAddress address, int port) {
        this.address = address;
        this.port = port;
    }

    public InetAddress getAddress() {
        return address;
    }

    public int getPort() {
        return port;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClientInfo that = (ClientInfo) o;
        return port == that.port && Objects.equals(address, that.address);
    }

    @Override
    public int hashCode() {
        return Objects.hash(address, port);
    }
}

public class Servidor {
    public static void main(String[] args) {
        final int PORTA = 8888;
        final int BUFFER_SIZE = 1024;

        Set<ClientInfo> clients = new HashSet<>();

        try (DatagramSocket socket = new DatagramSocket(PORTA)) {
            byte[] bufferReceber = new byte[BUFFER_SIZE];
            System.out.println("Servidor escutando na porta " + PORTA + "...");

            while (true) {
                DatagramPacket pacoteRecebido = new DatagramPacket(bufferReceber, bufferReceber.length);
                socket.receive(pacoteRecebido);

                // Armazenando dados do cliente
                ClientInfo clientInfo = new ClientInfo(pacoteRecebido.getAddress(), pacoteRecebido.getPort());
                clients.add(clientInfo);

                // Armazenando dados da mensagem
                String mensagemRecebida = new String(pacoteRecebido.getData(), 0, pacoteRecebido.getLength());
                System.out.println("Recebido de " + clientInfo.getAddress().getHostAddress() + ":" + clientInfo.getPort() + " -> " + mensagemRecebida);

                // Prepara os dados para o broadcast (a própria mensagem original)
                byte[] data = mensagemRecebida.getBytes();

                // Faz o broadcast para os clientes da lista (menos ao remetente)
                for (ClientInfo client : clients) {
                    if(pacoteRecebido.getPort() != client.getPort()) {
                        DatagramPacket pacoteResposta = new DatagramPacket(
                                data,
                                data.length,
                                client.getAddress(),
                                client.getPort()
                        );
                        socket.send(pacoteResposta);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
