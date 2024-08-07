import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

class Neighbour{
    private String ip;
    private int port;
    private String username;
    private HttpServer httpServer;

    public Neighbour(String ip, int port, String username) throws IOException {
        this.ip = ip;
        this.port = port;
        this.username = username;
        this.startHttpServer();
    }	

    public String getIp(){
        return this.ip;
    }

    public String getUsername(){
        return this.username;
    }

    public int getPort(){
        return this.port;
    }

    private void startHttpServer() throws IOException {
        httpServer = HttpServer.create(new InetSocketAddress(port + 1), 0);
        httpServer.createContext("/file", new FileHandler());
        httpServer.setExecutor(null); // creates a default executor
        httpServer.start();
        System.out.println("HTTP server started at port " + (port + 1));
    }

    static class FileHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String query = exchange.getRequestURI().getQuery();
            if (query == null || !query.startsWith("size=")) {
                exchange.sendResponseHeaders(400, -1);
                return;
            }

            int size;
            try {
                size = Integer.parseInt(query.split("=")[1]);
                if (size < 2 || size > 10) {
                    throw new IllegalArgumentException();
                }
            } catch (Exception e) {
                exchange.sendResponseHeaders(400, -1);
                return;
            }

            byte[] data = new byte[size * 1024 * 1024]; // Size in bytes
            new Random().nextBytes(data);

            String hash = calculateSHA256(data);
            String response = "File size: " + size + "MB\nSHA-256 Hash: " + hash;

            exchange.sendResponseHeaders(200, response.length());
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }

        private String calculateSHA256(byte[] data) {
            try {
                MessageDigest digest = MessageDigest.getInstance("SHA-256");
                byte[] hashBytes = digest.digest(data);
                StringBuilder sb = new StringBuilder();
                for (byte b : hashBytes) {
                    sb.append(String.format("%02x", b));
                }
                return sb.toString();
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }
        }
    }
}

public class BootstrapServer {   

    private static final String LOG_FILE = "server_log.txt"; // Path to log file
    private static Map<String, Integer> messageCounts = new HashMap<>(); // Track message counts per client

    public static void main(String[] args) {
        DatagramSocket sock = null;
        String s;
        List<Neighbour> nodes = new ArrayList<Neighbour>();

        try (FileWriter fileWriter = new FileWriter(LOG_FILE, true); // Append mode
             PrintWriter logWriter = new PrintWriter(fileWriter)) {

            sock = new DatagramSocket(55555);

            echo("Bootstrap Server created at 55555. Waiting for incoming data...");

            while(true)
            {
                byte[] buffer = new byte[65536];
                DatagramPacket incoming = new DatagramPacket(buffer, buffer.length);
                sock.receive(incoming);

                byte[] data = incoming.getData();
                s = new String(data, 0, incoming.getLength());

                //echo the details of incoming data - client ip : client port - client message
                echo(incoming.getAddress().getHostAddress() + " : " + incoming.getPort() + " - " + s);
                // Track message counts
                String clientKey = incoming.getAddress().getHostAddress() + ":" + incoming.getPort();
                messageCounts.put(clientKey, messageCounts.getOrDefault(clientKey, 0) + 1);

                // Measure latency
                long startTime = new Date().getTime();

                StringTokenizer st = new StringTokenizer(s, " ");

                String length = st.nextToken();
                String command = st.nextToken();
                String reply = "";
                long endTime = 0;

                if (command.equals("REG")) {
                    reply = "REGOK ";

                    String ip = st.nextToken();
                    int port = Integer.parseInt(st.nextToken());
                    String username = st.nextToken();
                    if (nodes.size() == 0) {
                        reply += "0";
                        nodes.add(new Neighbour(ip, port, username));
                    } else {
                        boolean isOkay = true;
                        for (int i=0; i<nodes.size(); i++) {
                            if (nodes.get(i).getPort() == port) {
                                if (nodes.get(i).getUsername().equals(username)) {
                                    reply += "9998";
                                } else {
                                    reply += "9997";
                                }
                                isOkay = false;
                            }
                        }
                        if (isOkay) {
                            if (nodes.size() == 1) {
                                reply += "1 " + nodes.get(0).getIp() + " " + nodes.get(0).getPort();
                            } else if (nodes.size() == 2) {
                                reply += "2 " + nodes.get(0).getIp() + " " + nodes.get(0).getPort() + " " + nodes.get(1).getIp() + " " + nodes.get(1).getPort();
                            } else {
                                Random r = new Random();
                                int Low = 0;
                                int High = nodes.size();
                                int random_1 = r.nextInt(High-Low) + Low;
                                int random_2 = r.nextInt(High-Low) + Low;
                                while (random_1 == random_2) {
                                    random_2 = r.nextInt(High-Low) + Low;
                                }
                                echo (random_1 + " " + random_2);
                                reply += "2 " + nodes.get(random_1).getIp() + " " + nodes.get(random_1).getPort() + " " + nodes.get(random_2).getIp() + " " + nodes.get(random_2).getPort();
                            }
                            nodes.add(new Neighbour(ip, port, username));
                        }
                    }

                    reply = String.format("%04d", reply.length() + 5) + " " + reply;

                    DatagramPacket dpReply = new DatagramPacket(reply.getBytes() , reply.getBytes().length , incoming.getAddress() , incoming.getPort());
                    sock.send(dpReply);
                } else if (command.equals("UNREG")) {
                    String ip = st.nextToken();
                    int port = Integer.parseInt(st.nextToken());
                    String username = st.nextToken();
                    boolean found = false;
                    for (int i=0; i<nodes.size(); i++) {
                        if (nodes.get(i).getPort() == port) {
                            nodes.remove(i);
                            reply = "0012 UNROK 0";
                            DatagramPacket dpReply = new DatagramPacket(reply.getBytes() , reply.getBytes().length , incoming.getAddress() , incoming.getPort());
                            sock.send(dpReply);
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        reply = "0012 UNROK 1"; // Registration not found
                    }

                } else if (command.equals("ECHO")) {
                    for (int i=0; i<nodes.size(); i++) {
                        echo(nodes.get(i).getIp() + " " + nodes.get(i).getPort() + " " + nodes.get(i).getUsername());
                    }
                     reply = "0012 ECHOK 0";
                    DatagramPacket dpReply = new DatagramPacket(reply.getBytes() , reply.getBytes().length , incoming.getAddress() , incoming.getPort());
                    sock.send(dpReply);
                    endTime = new Date().getTime();
                }
                long latency = endTime - startTime;

                // Log details to file
                logWriter.printf("Client IP: %s, Port: %d%n", incoming.getAddress().getHostAddress(), incoming.getPort());
                logWriter.printf("Received: %s%n", s);
                logWriter.printf("Response: %s%n", reply);
                logWriter.printf("Latency: %d ms%n", latency);
                logWriter.printf("Number of Hops: %d%n", getHopsFromMessage(s));
                logWriter.println("-------");
            }
        }

        catch(Exception e)
        {
            System.err.println("IOException " + e);
            e.printStackTrace();
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
