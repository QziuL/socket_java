import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;

public class Cliente {
    public static void main(String[] args) {
        final String ENDERECO_SERVIDOR = "localhost";
        final int PORTA_SERVIDOR = 8888;

        try (DatagramSocket socket = new DatagramSocket();
             Scanner scanner = new Scanner(System.in))
        {
            System.out.println("Digite seu nome:");
            String nome = scanner.nextLine();
            while (true) {
                System.out.print("Digite uma mensagem: ");
                String mensagem = scanner.nextLine();

                String dado = nome + ": " + mensagem;
                byte[] bufferEnvio = dado.getBytes();

                InetAddress enderecoServidor = InetAddress.getByName(ENDERECO_SERVIDOR);

                DatagramPacket pacoteEnvio = new DatagramPacket(
                        bufferEnvio,
                        bufferEnvio.length,
                        enderecoServidor,
                        PORTA_SERVIDOR
                );

                socket.send(pacoteEnvio);
            }

            // Receber resposta
//            byte[] bufferReceber = new byte[1024];
//            DatagramPacket pacoteResposta = new DatagramPacket(bufferReceber, bufferReceber.length);
//            socket.receive(pacoteResposta);
//
//            String resposta = new String(pacoteResposta.getData(), 0, pacoteResposta.getLength());
//            System.out.println("Resposta do servidor: " + resposta);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
