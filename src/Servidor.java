import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class Servidor {
    public static void main(String[] args) {
        final int PORTA = 8888;
        final int PORTA_ESCRITA = 9999;

        try (DatagramSocket socket = new DatagramSocket(PORTA);
             DatagramSocket socketWrite = new DatagramSocket(PORTA_ESCRITA))
        {
            byte[] bufferReceber = new byte[1024];

            System.out.println("Servidor escutando na porta " + PORTA + "...");

            while (true) {
                DatagramPacket pacoteRecebido = new DatagramPacket(bufferReceber, bufferReceber.length);
                socket.receive(pacoteRecebido);

                String mensagem = new String(pacoteRecebido.getData(), 0, pacoteRecebido.getLength());
                System.out.println("Mensagem recebida: [" + mensagem + "]");

                // Enviar resposta
                String resposta = "Servidor recebeu: " + mensagem;
                byte[] bufferResposta = resposta.getBytes();

                DatagramPacket pacoteResposta = new DatagramPacket(
                        bufferResposta,
                        bufferResposta.length,
                        pacoteRecebido.getAddress(),
                        pacoteRecebido.getPort()
                );

                socketWrite.send(pacoteResposta);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
