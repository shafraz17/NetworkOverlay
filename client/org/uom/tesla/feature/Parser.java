package org.uom.tesla.feature;

import org.uom.tesla.api.Constant;
import org.uom.tesla.api.Credential;
import org.uom.tesla.api.message.Message;
import org.uom.tesla.api.message.request.*;
import org.uom.tesla.api.message.response.*;

import java.util.*;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.StringTokenizer;
public class Parser {

    // Map to keep track of request timestamps for latency calculation
    private static final Map<Integer, Long> requestTimestamps = new HashMap<>();
    private static final String LOG_FILE_PATH = "latency_log.txt";
    public static Message parse(String message, Credential senderCredential) {

        System.out.println("Message received : " + message);
        StringTokenizer st = new StringTokenizer(message, " ");

        String length = st.nextToken();
        String command = st.nextToken();

        if (command.equals(Constant.Command.REG)) {
            String ip = st.nextToken();
            int port = Integer.parseInt(st.nextToken());
            String username = st.nextToken();
            Credential userCredentials = new Credential(ip, port, username);
            return new RegisterRequest(userCredentials);

        } else if (command.equals(Constant.Command.REGOK)) {
            int numOfNodes = Integer.parseInt(st.nextToken());
            String ip;
            int port;
            List<Credential> nodes = new ArrayList<>();
            if (!(numOfNodes == Constant.Codes.Register.ERROR_CANNOT_REGISTER || numOfNodes == Constant.Codes.Register.ERROR_DUPLICATE_IP || numOfNodes == Constant.Codes.Register.ERROR_ALREADY_REGISTERED || numOfNodes == Constant.Codes.Register.ERROR_COMMAND)) {
                for (int i = 0; i < numOfNodes; i++) {
                    ip = st.nextToken();
                    port = Integer.parseInt(st.nextToken());
                    nodes.add(new Credential(ip, port, null));
                }
            }
            RegisterResponse registerResponse = new RegisterResponse(numOfNodes, nodes);
            return registerResponse;

        } else if (command.equals(Constant.Command.UNREG)) {
            String ip = st.nextToken();
            int port = Integer.parseInt(st.nextToken());
            String username = st.nextToken();
            Credential unregUserCredentials = new Credential(ip, port, username);
            return new UnregisterRequest(unregUserCredentials);

        } else if (command.equals(Constant.Command.UNREGOK)) {
            int value = Integer.parseInt(st.nextToken());
            return new UnregisterResponse(value);

        } else if (command.equals(Constant.Command.LEAVE)) {
            String ip = st.nextToken();
            int port = Integer.parseInt(st.nextToken());
            Credential crd = new Credential(ip, port, null);
            return new LeaveRequest(crd);

        } else if (command.equals(Constant.Command.JOIN)) {
            String ip = st.nextToken();
            int port = Integer.parseInt(st.nextToken());
            Credential joinerCredentials = new Credential(ip, port, null);
            return new JoinRequest(joinerCredentials);

        } else if (command.equals(Constant.Command.JOINOK)) {
            int value = Integer.parseInt(st.nextToken());
            return new JoinResponse(value, senderCredential);

        } else if (command.equals(Constant.Command.LEAVEOK)) {
            int value = Integer.parseInt(st.nextToken());
            return new LeaveResponse(value);

        } else if (command.equals(Constant.Command.SEARCH)) {
            int seqNum = Integer.parseInt(st.nextToken());
            String ip = st.nextToken();
            int port = Integer.parseInt(st.nextToken());
            String fileName = st.nextToken();
            int hops = Integer.parseInt(st.nextToken());
            Credential crd = new Credential(ip, port, null);
            return new SearchRequest(seqNum, crd, fileName, hops);

        } else if (command.equals(Constant.Command.SEARCHOK)) {
            int sequenceNo = Integer.parseInt(st.nextToken());
            int numOfFiles = Integer.parseInt(st.nextToken());
            String ip = st.nextToken();
            int port = Integer.parseInt(st.nextToken());
            int hops = Integer.parseInt(st.nextToken());
            List<String> fileList = new ArrayList<>();
            if (numOfFiles > 0 && !(numOfFiles == Constant.Codes.Search.ERROR_OTHER || numOfFiles == Constant.Codes.Search.ERROR_NODE_UNREACHABLE)) {
                for (int i = 0; i < numOfFiles; i++) {
                    fileList.add(st.nextToken());
                }
            }
            Credential endNodeCredentials = new Credential(ip, port, null);
            SearchResponse searchResponse = new SearchResponse(sequenceNo, numOfFiles, endNodeCredentials, hops, fileList);
            System.out.println("latency called");
            logResponseLatency(searchResponse);
            return searchResponse;

        } else if (command.equals(Constant.Command.ERROR)) {
            return new ErrorResponse();
        }

        return null;
    }

    // Log response latency based on the request timestamp
    private static void logResponseLatency(Message response) {
        if (response instanceof ResponseWithSeqNum) {
            ResponseWithSeqNum responseWithSeqNum = (ResponseWithSeqNum) response;
            int sequenceNumber = responseWithSeqNum.getSequenceNumber();
            Long requestTime = requestTimestamps.remove(sequenceNumber);
            if (requestTime != null) {
                long latency = System.currentTimeMillis() - requestTime;
                String logMessage = "Latency for request " + sequenceNumber + ": " + latency + " ms";
                // Log to console
                System.out.println(logMessage);
                // Log to file
                writeLogToFile(logMessage);
            }
        }
    }

    // Track request timestamps
    public static void trackRequestTimestamp(int sequenceNumber) {
        requestTimestamps.put(sequenceNumber, System.currentTimeMillis());
    }

    // Write log message to a file
    private static void writeLogToFile(String logMessage) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(LOG_FILE_PATH, true))) {
            writer.write(logMessage);
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

 interface ResponseWithSeqNum {
    int getSequenceNumber();
}
