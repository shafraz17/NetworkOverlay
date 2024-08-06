import java.net.*;
import java.util.Scanner;
import java.util.Random;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class BootstrapClient {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        try {
            DatagramSocket socket = new DatagramSocket();
            InetAddress address = InetAddress.getByName("localhost"); // Change to server's address if needed
            int serverPort = 55555;

            while (true) {
                System.out.println("Enter command (REG, UNREG, ECHO, DOWNLOAD, or QUIT to exit):");
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
                    case "DOWNLOAD":
                        System.out.println("Enter the IP of the node:");
                        ip = scanner.nextLine();
                        System.out.println("Enter the port of the node:");
                        port = Integer.parseInt(scanner.nextLine());
//                        scanner.close();
                        downloadFile(ip, port);
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

    private static void downloadFile(String ip, Integer port) {
        try {
            int size = new Random().nextInt(7) + 3; // Generate a size between 2 and 10 MB
            String url = "http:" + ip + ":" + port + 1 + "file?size=" + size;

            URL obj = URI.create(url).toURL();
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("GET");

            int responseCode = con.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) { // success
                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine).append("\n");
                }
                in.close();

                System.out.printf("File size: " + size);
                System.out.println("Response:" + response.toString());
            } else {
                System.out.println("GET request not worked");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}