import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;

public class BootstrapClient {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        try {
            DatagramSocket socket = new DatagramSocket();
            InetAddress address = InetAddress.getByName("localhost"); // Change to server's address if needed
            int serverPort = 55555;

            while (true) {
                System.out.println("Enter command (REG, UNREG, ECHO, or QUIT to exit):");
                String command = scanner.nextLine();

                if (command.equalsIgnoreCase("QUIT")) {
                    break;
                }

                String message = "";
                switch (command) {
                    case "REG":
                        System.out.println("Enter your IP:");
                        String ip = scanner.nextLine();
                        System.out.println("Enter your port:");
                        int port = Integer.parseInt(scanner.nextLine());
                        System.out.println("Enter your username:");
                        String username = scanner.nextLine();
                        message = String.format("0048 REG %s %d %s", ip, port, username);
                        break;
                    case "UNREG":
                        System.out.println("Enter your IP:");
                        ip = scanner.nextLine();
                        System.out.println("Enter your port:");
                        port = Integer.parseInt(scanner.nextLine());
                        System.out.println("Enter your username:");
                        username = scanner.nextLine();
                        message = String.format("0048 UNREG %s %d %s", ip, port, username);
                        break;
                    case "ECHO":
                        message = "0012 ECHO";
                        break;
                    default:
                        System.out.println("Invalid command!");
                        continue;
                }

                byte[] buffer = message.getBytes();
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, serverPort);
                socket.send(packet);

                byte[] recvBuffer = new byte[65536];
                DatagramPacket recvPacket = new DatagramPacket(recvBuffer, recvBuffer.length);
                socket.receive(recvPacket);

                String response = new String(recvPacket.getData(), 0, recvPacket.getLength());
                System.out.println("Response from server: " + response);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            scanner.close();
        }
    }
}