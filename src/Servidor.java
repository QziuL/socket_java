import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Servidor extends Thread {
    public static void main(String[] args) {
        Servidor servidor = new Servidor();
        servidor.start();
    }

    public void run() {
        final int PORTA = 8888;
        final int BUFFER_SIZE = 1024;

        Set<String> clients = new HashSet<>();
        List<Integer> ports = new ArrayList<>();
        List<InetAddress> addresses = new ArrayList<>();

        try (DatagramSocket socket = new DatagramSocket(PORTA))
        {
            byte[] bufferReceber = new byte[BUFFER_SIZE];

            System.out.println("Servidor escutando na porta " + PORTA + "...");

            while (true) {
                DatagramPacket pacoteRecebido = new DatagramPacket(bufferReceber, bufferReceber.length);
                socket.receive(pacoteRecebido);

                String id = pacoteRecebido.getAddress().getHostAddress() + ',' + pacoteRecebido.getPort();
                if (!clients.contains(id)) {
                    clients.add(id);
                    ports.add(pacoteRecebido.getPort());
                    addresses.add(pacoteRecebido.getAddress());
                }

                String mensagem = new String(pacoteRecebido.getData(), 0, pacoteRecebido.getLength());
                System.out.println("Mensagem recebida: [" + mensagem + "]");

                if(addresses.size() < 2) {
                    DatagramPacket pacoteResposta;
                    byte[] data = ("Você está sozinho...").getBytes();
                    pacoteResposta = new DatagramPacket(
                            data,
                            data.length,
                            addresses.getFirst(),
                            ports.getFirst()
                    );
                    socket.send(pacoteResposta);
                } else {
                    DatagramPacket pacoteResposta;
                    byte[] data = (id + " : " +  mensagem).getBytes();
                    for(int i = 0; i < addresses.size(); i++) {
                        if(addresses.get(i) != pacoteRecebido.getAddress()) {
                            InetAddress address = addresses.get(i);
                            int port = ports.get(i);
                            pacoteResposta = new DatagramPacket(
                                    data,
                                    data.length,
                                    address,
                                    port
                            );
                            socket.send(pacoteResposta);
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
