import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;


class Sender implements Runnable {
    public final static int PORT_SERVER = 8888;
    public final static String ADDRESS_SERVER = "localhost";

    public void run() {
        try (DatagramSocket socket = new DatagramSocket();
             Scanner scanner = new Scanner(System.in))
        {
            System.out.println("Digite seu nome:");
            String nome = scanner.nextLine();
            while (true) {
                System.out.print("Digite uma mensagem: ");
                String mensagem = scanner.nextLine();

                LocalDateTime data = LocalDateTime.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
                String hora = data.format(formatter);

                String dado = "[" + hora + "]" + nome + ": " + mensagem;
                byte[] bufferEnvio = dado.getBytes();

                InetAddress enderecoServidor = InetAddress.getByName(ADDRESS_SERVER);

                DatagramPacket pacoteEnvio = new DatagramPacket(
                        bufferEnvio,
                        bufferEnvio.length,
                        enderecoServidor,
                        PORT_SERVER
                );

                socket.send(pacoteEnvio);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

class Receiver implements Runnable {
    public static final byte[] buffer = new byte[1024];

    public void run() {
        try(DatagramSocket socket = new DatagramSocket()){
            while (true) {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);

                String received = new String(packet.getData(), 0, packet.getLength());
                System.out.println(received);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

public class Cliente {
    public static void main(String[] args) throws InterruptedException {
        Thread senderThread = new Thread(new Sender());
        Thread receiverThread = new Thread(new Receiver());

        senderThread.start();
        receiverThread.start();

        senderThread.join();
        receiverThread.join();
    }
}

