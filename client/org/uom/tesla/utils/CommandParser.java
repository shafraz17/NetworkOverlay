package org.uom.tesla.utils;

import org.uom.tesla.model.NodeMeta;
import org.uom.tesla.model.Message;
import org.uom.tesla.model.payload.*;
import org.uom.tesla.model.response.*;
import org.uom.tesla.model.Error;
import org.uom.tesla.model.response.JoinData;
import org.uom.tesla.model.response.LeaveData;
import org.uom.tesla.model.response.RegisterData;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class CommandParser {

    public static Message parse(String message, NodeMeta senderNodeMeta) {

        System.out.println("Message received : " + message);
        StringTokenizer st = new StringTokenizer(message, " ");

        String length = st.nextToken();
        String command = st.nextToken();

        switch (command) {
            case Constants.REG -> {
                String ip = st.nextToken();
                int port = Integer.parseInt(st.nextToken());
                String username = st.nextToken();
                NodeMeta userCredentials = new NodeMeta(ip, port, username);
                return new org.uom.tesla.model.payload.Register(userCredentials);

            }
            case Constants.REG_OK -> {
                int numOfNodes = Integer.parseInt(st.nextToken());
                String ip;
                int port;
                List<NodeMeta> nodes = new ArrayList<>();
                if (!(numOfNodes == Constants.ERROR_CANNOT_REGISTER || numOfNodes == Constants.ERROR_DUPLICATE_IP || numOfNodes == Constants.ERROR_ALREADY_REGISTERED || numOfNodes == Constants.ERROR_COMMAND)) {
                    for (int i = 0; i < numOfNodes; i++) {
                        ip = st.nextToken();
                        port = Integer.parseInt(st.nextToken());
                        nodes.add(new NodeMeta(ip, port, null));
                    }
                }
                return new RegisterData(numOfNodes, nodes);

            }
            case Constants.UN_REG -> {
                String ip = st.nextToken();
                int port = Integer.parseInt(st.nextToken());
                String username = st.nextToken();
                NodeMeta unregUserCredentials = new NodeMeta(ip, port, username);
                return new Unregister(unregUserCredentials);

            }
            case Constants.UN_REG_OK -> {
                int value = Integer.parseInt(st.nextToken());
                return new UnregisterData(value);

            }
            case Constants.LEAVE -> {
                String ip = st.nextToken();
                int port = Integer.parseInt(st.nextToken());
                NodeMeta crd = new NodeMeta(ip, port, null);
                return new org.uom.tesla.model.payload.Leave(crd);

            }
            case Constants.JOIN -> {
                String ip = st.nextToken();
                int port = Integer.parseInt(st.nextToken());
                NodeMeta joinerCredentials = new NodeMeta(ip, port, null);
                return new org.uom.tesla.model.payload.Join(joinerCredentials);

            }
            case Constants.JOIN_OK -> {
                int value = Integer.parseInt(st.nextToken());
                return new JoinData(value, senderNodeMeta);

            }
            case Constants.LEAVE_OK -> {
                int value = Integer.parseInt(st.nextToken());
                return new LeaveData(value);

            }
            case Constants.SEARCH -> {
                int seqNum = Integer.parseInt(st.nextToken());
                String ip = st.nextToken();
                int port = Integer.parseInt(st.nextToken());
                String fileName = st.nextToken();
                int hops = Integer.parseInt(st.nextToken());
                NodeMeta crd = new NodeMeta(ip, port, null);
                return new Search(seqNum, crd, fileName, hops);

            }
            case Constants.SEARCH_OK -> {
                int sequenceNo = Integer.parseInt(st.nextToken());
                int numOfFiles = Integer.parseInt(st.nextToken());
                String ip = st.nextToken();
                int port = Integer.parseInt(st.nextToken());
                int hops = Integer.parseInt(st.nextToken());
                List<String> fileList = new ArrayList<>();
                if (numOfFiles > 0 && !(numOfFiles == Constants.ERROR_OTHER || numOfFiles == Constants.ERROR_NODE_UNREACHABLE)) {
                    for (int i = 0; i < numOfFiles; i++) {
                        fileList.add(st.nextToken());
                    }
                }
                NodeMeta endNodeCredentials = new NodeMeta(ip, port, null);
                return new SearchData(sequenceNo, numOfFiles, endNodeCredentials, hops, fileList);

            }
            case Constants.ERROR -> {
                return new Error();
            }
        }

        return null;
    }
}
