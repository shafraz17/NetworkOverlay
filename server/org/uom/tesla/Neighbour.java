package org.uom.tesla;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

class Neighbour {
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

    public String getIp() {
        return this.ip;
    }

    public String getUsername() {
        return this.username;
    }

    public int getPort() {
        return this.port;
    }

    private void startHttpServer() throws IOException {
        httpServer = HttpServer.create(new InetSocketAddress(this.ip,this.port + 1), 0);
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
