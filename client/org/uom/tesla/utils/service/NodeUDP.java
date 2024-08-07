package org.uom.tesla.utils.service;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.uom.tesla.model.payload.*;
import org.uom.tesla.model.response.*;
import org.uom.tesla.model.Error;
import org.uom.tesla.model.response.JoinData;
import org.uom.tesla.model.response.LeaveData;
import org.uom.tesla.model.response.RegisterData;
import org.uom.tesla.utils.CommandParser;
import org.uom.tesla.model.NodeMeta;
import org.uom.tesla.model.Node;
import org.uom.tesla.model.Message;
import org.uom.tesla.utils.Constants;

import java.io.IOException;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import java.net.*;
import java.util.Random;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class NodeUDP implements org.uom.tesla.utils.NodeUDP, Runnable {

    private final Node node;
    private final NodeMeta bootstrapServerNodeMeta;
    private DatagramSocket socket;
    private boolean regOk = false;
    private HttpServer httpServer;

    public NodeUDP(NodeMeta bootstrapServerNodeMeta, NodeMeta nodeNodeMeta) {
        this.bootstrapServerNodeMeta = bootstrapServerNodeMeta;

        this.node = new Node();
        node.setCredential(nodeNodeMeta);
        node.setFileList(createFileList());
        node.setRoutingTable(new ArrayList<>());
        node.setStatTable(new ArrayList<>());

        this.start();
    }

    public Node getNode() {
        return node;
    }

    @Override
    public void run() {
        System.out.println("Server " + this.node.getCredential().getUsername() + " created at " + this.node.getCredential().getPort() + ". Waiting for incoming data...");
        byte[] buffer;
        DatagramPacket datagramPacket;
        while (true) {
            buffer = new byte[65536];
            datagramPacket = new DatagramPacket(buffer, buffer.length);
            try {
                socket.receive(datagramPacket);
                String message = new String(datagramPacket.getData(), 0, datagramPacket.getLength());
                NodeMeta senderNodeMeta = new NodeMeta(datagramPacket.getAddress().getHostAddress(), datagramPacket.getPort(), null);
                Message response = CommandParser.parse(message, senderNodeMeta);
                processResponse(response);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void start() {
        try {
            socket = new DatagramSocket(this.node.getCredential().getPort());
        } catch (SocketException e) {
            e.printStackTrace();
        }
        new Thread(this).start();
    }

    @Override
    public void register() {
        org.uom.tesla.model.payload.Register registerRequest = new org.uom.tesla.model.payload.Register(node.getCredential());
        String msg = registerRequest.getMessageAsString(Constants.REG);
        try {
            socket.send(new DatagramPacket(msg.getBytes(), msg.getBytes().length, InetAddress.getByName(bootstrapServerNodeMeta.getIp()), bootstrapServerNodeMeta.getPort()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void unRegister() {
        Unregister unregister = new Unregister(node.getCredential());
        String msg = unregister.getMessageAsString(Constants.UN_REG);
        try {
            socket.send(new DatagramPacket(msg.getBytes(), msg.getBytes().length, InetAddress.getByName(bootstrapServerNodeMeta.getIp()), bootstrapServerNodeMeta.getPort()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void join(NodeMeta neighbourNodeMeta) {
        org.uom.tesla.model.payload.Join join = new org.uom.tesla.model.payload.Join(node.getCredential());
        String msg = join.getMessageAsString(Constants.JOIN);
        try {
            socket.send(new DatagramPacket(msg.getBytes(), msg.getBytes().length, InetAddress.getByName(neighbourNodeMeta.getIp()), neighbourNodeMeta.getPort()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void joinOk(NodeMeta senderNodeMeta) {
        JoinData joinResponse = new JoinData(0, node.getCredential());
        String msg = joinResponse.getMessageAsString(Constants.JOIN_OK);
        try {
            socket.send(new DatagramPacket(msg.getBytes(), msg.getBytes().length, InetAddress.getByName(senderNodeMeta.getIp()), senderNodeMeta.getPort()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void leave(NodeMeta neighbourNodeMeta) {
        org.uom.tesla.model.payload.Leave leave = new org.uom.tesla.model.payload.Leave(node.getCredential());
        String msg = leave.getMessageAsString(Constants.LEAVE);
        try {
            socket.send(new DatagramPacket(msg.getBytes(), msg.getBytes().length, InetAddress.getByName(neighbourNodeMeta.getIp()), neighbourNodeMeta.getPort()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void leaveOk(NodeMeta senderCredentials) {
        LeaveData leaveResponse = new LeaveData(0);
        String msg = leaveResponse.getMessageAsString(Constants.LEAVE_OK);
        try {
            socket.send(new DatagramPacket(msg.getBytes(), msg.getBytes().length, InetAddress.getByName(senderCredentials.getIp()), senderCredentials.getPort()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void search(Search search, NodeMeta sendCredentials) {
        String msg = search.getMessageAsString(Constants.SEARCH);
        try {
            socket.send(new DatagramPacket(msg.getBytes(), msg.getBytes().length, InetAddress.getByName(sendCredentials.getIp()), sendCredentials.getPort()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void searchOk(SearchData searchResponse) {
        String msg = searchResponse.getMessageAsString(Constants.SEARCH_OK);
        try {
            socket.send(new DatagramPacket(msg.getBytes(), msg.getBytes().length, InetAddress.getByName(searchResponse.getCredential().getIp()), searchResponse.getCredential().getPort()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void error(NodeMeta senderNodeMeta) {
        Error errorResponse = new Error();
        String msg = errorResponse.getMessageAsString(Constants.ERROR);
        try {
            socket.send(new DatagramPacket(msg.getBytes(), msg.getBytes().length, InetAddress.getByName(senderNodeMeta.getIp()), senderNodeMeta.getPort()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<String> createFileList() {
        ArrayList<String> fileList = new ArrayList<>();
        fileList.add("Adventures_of_Tintin");
        fileList.add("Jack_and_Jill");
        fileList.add("Glee");
        fileList.add("The_Vampire Diarie");
        fileList.add("King_Arthur");
        fileList.add("Windows_XP");
        fileList.add("Harry_Potter");
        fileList.add("Kung_Fu_Panda");
        fileList.add("Lady_Gaga");
        fileList.add("Twilight");
        fileList.add("Windows_8");
        fileList.add("Mission_Impossible");
        fileList.add("Turn_Up_The_Music");
        fileList.add("Super_Mario");
        fileList.add("American_Pickers");
        fileList.add("Microsoft_Office_2010");
        fileList.add("Happy_Feet");
        fileList.add("Modern_Family");
        fileList.add("American_Idol");
        fileList.add("Hacking_for_Dummies");
        Collections.shuffle(fileList);
        List<String> subFileList = fileList.subList(0, 5);
        System.out.println("File List : " + Arrays.toString(subFileList.toArray()));
        return subFileList;
    }

    @Override
    public void processResponse(Message response) {
        if (response instanceof RegisterData) {
            RegisterData registerResponse = (RegisterData) response;
            if (registerResponse.getNoOfNodes() == Constants.ERROR_ALREADY_REGISTERED) {
                System.out.println("Already registered at Bootstrap with same username");
                NodeMeta nodeMeta = node.getCredential();
                nodeMeta.setUsername(UUID.randomUUID().toString());
                node.setCredential(nodeMeta);
                register();
            } else if (registerResponse.getNoOfNodes() == Constants.ERROR_DUPLICATE_IP) {
                System.out.println("Already registered at Bootstrap with same port");
                NodeMeta nodeMeta = node.getCredential();
                nodeMeta.setPort(nodeMeta.getPort() + 1);
                node.setCredential(nodeMeta);
                register();
            } else if (registerResponse.getNoOfNodes() == Constants.ERROR_CANNOT_REGISTER) {
                System.out.print("Can’t register. Bootstrap server full. Try again later");
            } else if (registerResponse.getNoOfNodes() == Constants.ERROR_COMMAND) {
                System.out.println("Error in command");
            } else {
                List<NodeMeta> nodeMetaList = registerResponse.getCredentials();
                ArrayList<NodeMeta> routingTable = new ArrayList<>(nodeMetaList);
                printRoutingTable(routingTable);
                this.node.setRoutingTable(routingTable);
                this.regOk = true;
            }

        } else if (response instanceof UnregisterData) {
            node.setRoutingTable(new ArrayList<>());
            node.setFileList(new ArrayList<>());
            node.setStatTable(new ArrayList<>());
            this.regOk = false;

        } else if (response instanceof Search search) {
            triggerSearchRequest(search);
        } else if (response instanceof SearchData searchResponse) {
            if (searchResponse.getNoOfFiles() == Constants.ERROR_NODE_UNREACHABLE) {
                System.out.println("Failure due to node unreachable");
            } else if (searchResponse.getNoOfFiles() == Constants.ERROR_OTHER) {
                System.out.println("Some other error");
            } else {
                System.out.println("--------------------------------------------------------");
                System.out.println(searchResponse);
                System.out.println("--------------------------------------------------------");
            }
        } else if (response instanceof org.uom.tesla.model.payload.Join) {
            joinOk(node.getCredential());
        } else if (response instanceof JoinData joinResponse) {
            List<NodeMeta> routingTable = node.getRoutingTable();
            routingTable.add(joinResponse.getSenderCredential());
            node.setRoutingTable(routingTable);
        } else if (response instanceof Leave leave) {
            List<NodeMeta> routingTable = node.getRoutingTable();
            routingTable.remove(leave.getCredential());
            node.setRoutingTable(routingTable);

        } else if (response instanceof Error) {
            Error errorResponse = (Error) response;
            System.out.println(errorResponse.toString());
        }
    }

    @Override
    public boolean isRegOk() {
        return regOk;
    }

    @Override
    public List<String> checkForFiles(String fileName, List<String> fileList) {
        Pattern pattern = Pattern.compile(fileName);
        return fileList.stream().filter(pattern.asPredicate()).collect(Collectors.toList());
    }

    @Override
    public void printRoutingTable(List<NodeMeta> routingTable) {
        System.out.println("Routing table updated as :");
        System.out.println("--------------------------------------------------------");
        System.out.println("IP \t \t \t PORT");
        for (NodeMeta nodeMeta : routingTable) {
            System.out.println(nodeMeta.getIp() + "\t" + nodeMeta.getPort());
        }
        System.out.println("--------------------------------------------------------");
    }

    @Override
    public void triggerSearchRequest(Search search) {
        System.out.println("\nTriggered search request for " + search.getFileName());
        List<String> searchResult = checkForFiles(search.getFileName(), node.getFileList());
        if (!searchResult.isEmpty()) {
            System.out.println("File is available at " + node.getCredential().getIp() + " : " + node.getCredential().getPort());
            SearchData searchResponse = new SearchData(search.getSequenceNo(), searchResult.size(), search.getCredential(), search.getHops(), searchResult);

            downloadFile(node.getCredential().getIp(), node.getCredential().getPort() + 1);

            if (search.getCredential().getIp() == node.getCredential().getIp() && search.getCredential().getPort() == node.getCredential().getPort()) {
                System.out.println(searchResponse.toString());
            } else {
                System.out.println("Send SEARCHOK response message");
                searchOk(searchResponse);
            }

        } else {
            System.out.println("File is not available at " + node.getCredential().getIp() + " : " + node.getCredential().getPort());
            search.setHops(search.incHops());
            for (NodeMeta nodeMeta : node.getRoutingTable()) {
                search(search, nodeMeta);
                System.out.println("Send SER request message to " + nodeMeta.getIp() + " : " + nodeMeta.getPort());
            }
        }
    }

    public void startHttpServer() throws IOException {
        httpServer = HttpServer.create(new InetSocketAddress(node.getCredential().getPort() + 1), 0);
        httpServer.createContext("/file", new FileHandler());
        httpServer.setExecutor(null); // creates a default executor
        httpServer.start();
        System.out.println("HTTP server started at port " + (node.getCredential().getPort() + 1));
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

    private static void downloadFile(String ip, Integer port) {
        try {
            int size = new Random().nextInt(7) + 3; // Generate a size between 2 and 10 MB
            String url = "http:" + ip + ":" + port + "/file?size=" + size;
//            String url = "http:" + "localhost" + ":" + port + "/file?size=" + size;

            URL obj = URI.create(url).toURL();
            HttpURLConnection con = (HttpURLConnection) obj.openConnection(Proxy.NO_PROXY);
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
