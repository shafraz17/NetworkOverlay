import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

class Neighbour {
    private String ip;
    private int port;
    private String username;

    public Neighbour(String ip, int port, String username) {
        this.ip = ip;
        this.port = port;
        this.username = username;
    }

    public String getIp() {
        return this.ip;
    }

    public String getUsername() {
        return this.username;
    }

    public int getPort() {
        return this.port;
    }
}

public class BootstrapServer {

    private static final String LOG_FILE = "server_log.txt"; // Path to log file
    private static Map<String, Integer> messageCounts = new HashMap<>(); // Track message counts per client

    public static void main(String[] args) {
        DatagramSocket sock = null;
        String s;
        List<Neighbour> nodes = new ArrayList<>();

        try (FileWriter fileWriter = new FileWriter(LOG_FILE, true); // Append mode
             PrintWriter logWriter = new PrintWriter(fileWriter)) {

            sock = new DatagramSocket(55555);

            echo("Bootstrap Server created at 55555. Waiting for incoming data...");

            while (true) {
                byte[] buffer = new byte[65536];
                DatagramPacket incoming = new DatagramPacket(buffer, buffer.length);
                sock.receive(incoming);

                byte[] data = incoming.getData();
                s = new String(data, 0, incoming.getLength());

                // Track message counts
                String clientKey = incoming.getAddress().getHostAddress() + ":" + incoming.getPort();
                messageCounts.put(clientKey, messageCounts.getOrDefault(clientKey, 0) + 1);

                // Measure latency
                long startTime = new Date().getTime();

                StringTokenizer st = new StringTokenizer(s, " ");
                String length = st.nextToken();
                String command = st.nextToken();
                String reply = "";

                if (command.equals("REG")) {
                    reply = "REGOK ";

                    String ip = st.nextToken();
                    int port = Integer.parseInt(st.nextToken());
                    String username = st.nextToken();
                    int hops = Integer.parseInt(st.nextToken());

                    boolean isOkay = true;
                    for (Neighbour neighbour : nodes) {
                        if (neighbour.getPort() == port) {
                            if (neighbour.getUsername().equals(username)) {
                                reply += "9998"; // Duplicate username at same port
                            } else {
                                reply += "9997"; // Port conflict
                            }
                            isOkay = false;
                            break;
                        }
                    }

                    if (isOkay) {
                        if (nodes.size() == 0) {
                            reply += "0";
                        } else if (nodes.size() == 1) {
                            reply += "1 " + nodes.get(0).getIp() + " " + nodes.get(0).getPort();
                        } else if (nodes.size() == 2) {
                            reply += "2 " + nodes.get(0).getIp() + " " + nodes.get(0).getPort() + " " + nodes.get(1).getIp() + " " + nodes.get(1).getPort();
                        } else {
                            Random r = new Random();
                            int low = 0;
                            int high = nodes.size();
                            int random1 = r.nextInt(high - low) + low;
                            int random2 = r.nextInt(high - low) + low;
                            while (random1 == random2) {
                                random2 = r.nextInt(high - low) + low;
                            }
                            reply += "2 " + nodes.get(random1).getIp() + " " + nodes.get(random1).getPort() + " " + nodes.get(random2).getIp() + " " + nodes.get(random2).getPort();
                        }
                        nodes.add(new Neighbour(ip, port, username));
                    }

                    reply = String.format("%04d", reply.length() + 5) + " " + reply;

                } else if (command.equals("UNREG")) {
                    String ip = st.nextToken();
                    int port = Integer.parseInt(st.nextToken());
                    String username = st.nextToken();
                    boolean found = false;
                    for (int i = 0; i < nodes.size(); i++) {
                        if (nodes.get(i).getPort() == port && nodes.get(i).getUsername().equals(username)) {
                            nodes.remove(i);
                            reply = "0012 UNROK 0";
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        reply = "0012 UNROK 1"; // Registration not found
                    }

                } else if (command.equals("ECHO")) {
                    for (Neighbour neighbour : nodes) {
                        echo(neighbour.getIp() + " " + neighbour.getPort() + " " + neighbour.getUsername());
                    }
                    reply = "0012 ECHOK 0";
                }

                long endTime = new Date().getTime();
                long latency = endTime - startTime;

                // Send the response
                DatagramPacket dpReply = new DatagramPacket(reply.getBytes(), reply.getBytes().length, incoming.getAddress(), incoming.getPort());
                sock.send(dpReply);

                // Log details to file
                logWriter.printf("Client IP: %s, Port: %d%n", incoming.getAddress().getHostAddress(), incoming.getPort());
                logWriter.printf("Received: %s%n", s);
                logWriter.printf("Response: %s%n", reply);
                logWriter.printf("Latency: %d ms%n", latency);
                logWriter.printf("Number of Hops: %d%n", getHopsFromMessage(s));
                logWriter.println("-------");
            }
        } catch (IOException e) {
            System.err.println("IOException " + e);
        }
    }

    // Extract hop count from the message
    private static int getHopsFromMessage(String message) {
        StringTokenizer st = new StringTokenizer(message, " ");
        while (st.hasMoreTokens()) {
            String token = st.nextToken();
            if (token.matches("\\d+")) {
                return Integer.parseInt(token);
            }
        }
        return 0;
    }

    // Simple function to echo data to terminal
    public static void echo(String msg) {
        System.out.println(msg);
    }
}
