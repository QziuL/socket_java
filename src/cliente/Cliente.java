package cliente;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class Cliente {
    public static void main(String[] args) throws InterruptedException {
        int PORT_SERVER = 8888;
        String ADDRESS_SERVER = "localhost";

        try (DatagramSocket socket = new DatagramSocket();
             Scanner scanner = new Scanner(System.in))
        {
            System.out.println("Digite seu nome:");
            String nome = scanner.nextLine();

            // --- THREAD DE RECEBIMENTO (LISTENER) ---
            // Esta thread usará o mesmo socket para escutar.
            Thread receiverThread = new Thread(() -> {
                byte[] buffer = new byte[1024];
                while (!Thread.currentThread().isInterrupted()) {
                    try {
                        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                        socket.receive(packet); // Escuta no socket compartilhado

                        String received = new String(packet.getData(), 0, packet.getLength());
                        System.out.println(received);
                    } catch (IOException e) {
                        // Quando o socket for fechado na thread principal, uma exceção será lançada.
                        System.out.println("Saindo do chat...");
                        break;
                    }
                }
            });
            receiverThread.start();

            // --- THREAD DE ENVIO (SENDER - na thread principal) ---
            System.out.println("Chat iniciado. Digite 'sair' para encerrar.");

            while (true) {
                String mensagem = scanner.nextLine();

                if ("sair".equalsIgnoreCase(mensagem)) {
                    receiverThread.interrupt(); // Sinaliza para a outra thread parar
                    break; // Sai do loop de envio
                }

                // Formata a mensagem
                LocalDateTime data = LocalDateTime.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
                String hora = data.format(formatter);
                String dado = "[" + hora + "] " + nome + ": " + mensagem;

                byte[] bufferEnvio = dado.getBytes();
                InetAddress enderecoServidor = InetAddress.getByName(ADDRESS_SERVER);

                DatagramPacket pacoteEnvio = new DatagramPacket(
                        bufferEnvio,
                        bufferEnvio.length,
                        enderecoServidor,
                        PORT_SERVER
                );

                socket.send(pacoteEnvio); // Envia pelo socket compartilhado
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

