import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Date;
import java.util.Scanner;

public class BootstrapClient {

    private static final String LOG_FILE = "client_log.txt";
    private static int hopCounter = 0;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        try (DatagramSocket socket = new DatagramSocket();
             FileWriter fileWriter = new FileWriter(LOG_FILE, true); // Append mode
             PrintWriter logWriter = new PrintWriter(fileWriter)) {

            InetAddress address = InetAddress.getByName("23.94.83.39");
            int serverPort = 55555;

            while (true) {
                System.out.println("Enter command (REG, UNREG, ECHO, or QUIT to exit):");
                String command = scanner.nextLine();

                if (command.equalsIgnoreCase("QUIT")) {
                    logWriter.println("Client exited.");
                    break;
                }

                String message = "";
                String requestMessage = "";
                switch (command) {
                    case "REG":
                        System.out.println("Enter your IP:");
                        String ip = scanner.nextLine();
                        System.out.println("Enter your port:");
                        int port = Integer.parseInt(scanner.nextLine());
                        System.out.println("Enter your username:");
                        String username = scanner.nextLine();
                        message = String.format("0048 REG %s %d %s %d", ip, port, username, hopCounter);
                        requestMessage = String.format("REG request: IP=%s, Port=%d, Username=%s, Hops=%d", ip, port, username, hopCounter);
                        break;
                    case "UNREG":
                        System.out.println("Enter your IP:");
                        ip = scanner.nextLine();
                        System.out.println("Enter your port:");
                        port = Integer.parseInt(scanner.nextLine());
                        System.out.println("Enter your username:");
                        username = scanner.nextLine();
                        message = String.format("0048 UNREG %s %d %s %d", ip, port, username, hopCounter);
                        requestMessage = String.format("UNREG request: IP=%s, Port=%d, Username=%s, Hops=%d", ip, port, username, hopCounter);
                        break;
                    case "ECHO":
                        message = String.format("0012 ECHO %d", hopCounter);
                        requestMessage = String.format("ECHO request with Hops=%d", hopCounter);
                        break;
                    default:
                        System.out.println("Invalid command!");
                        continue;
                }

                // Print request details to console
                System.out.println("================================");
                System.out.println("Sending request:");
                System.out.println("Command: " + command);
                System.out.println("Message: " + message);
                System.out.println("Request Details: " + requestMessage);

                // Send the request message
                byte[] buffer = message.getBytes();
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, serverPort);
                socket.send(packet);

                // Measure latency
                long startTime = new Date().getTime();
                byte[] recvBuffer = new byte[65536];
                DatagramPacket recvPacket = new DatagramPacket(recvBuffer, recvBuffer.length);
                socket.receive(recvPacket);
                long endTime = new Date().getTime();

                // Extract and print response
                String response = new String(recvPacket.getData(), 0, recvPacket.getLength());
                System.out.println("Response from server: " + response);
                System.out.println("Latency: " + (endTime - startTime) + " ms");
                System.out.println("================================");
                System.out.println(" ");

                // Log details to file
                logWriter.printf("Request: %s%n", requestMessage);
                logWriter.printf("Response: %s%n", response);
                logWriter.printf("Latency: %d ms%n", endTime - startTime);
                logWriter.printf("Hops: %d%n", hopCounter);
                logWriter.println("-------");

                // Increment hop counter for the next request
                hopCounter++;
            }
        } catch (Exception e) {
            System.out.println(e);
        } finally {
            scanner.close();
        }
    }
}
