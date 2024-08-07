package org.uom.tesla;

import org.uom.tesla.model.NodeMeta;
import org.uom.tesla.model.payload.Search;
import org.uom.tesla.utils.Constants;
import org.uom.tesla.utils.service.NodeUDP;

import java.util.*;
public class TestClient {
    public static void main(String[] args) {
        HashMap<String, String> paramMap = new HashMap<>();

        for (int i = 0; i < args.length; i = i + 2) {
            paramMap.put(args[i], args[i + 1]);
            System.out.println(args[i] + " : " + args[i + 1]);
        }

        System.out.println();

        String bootstrapIp = paramMap.get("-b") != null ? paramMap.get("-b") : Constants.IP_BOOTSTRAP_SERVER;
        String nodeIp = paramMap.get("-i") != null ? paramMap.get("-i") : Constants.IP_BOOTSTRAP_SERVER;
        int nodePort = paramMap.get("-p") != null ? Integer.parseInt(paramMap.get("-p")) : new Random().nextInt(Constants.MAX_PORT_NODE - Constants.MIN_PORT_NODE) + Constants.MIN_PORT_NODE;
        String nodeUsername = paramMap.get("-u") != null ? paramMap.get("-u") : UUID.randomUUID().toString();

        NodeMeta bootstrapServerNodeMeta = new NodeMeta(bootstrapIp, Constants.PORT_BOOTSTRAP_SERVER, Constants.USERNAME_BOOTSTRAP_SERVER);
        Map<Integer, String> searchQueryTable = new HashMap<>();
        List<String> searchQueries = Arrays.asList("Twilight", "Jack", "American_Idol", "Happy_Feet", "Twilight_saga", "Happy_Feet", "Feet");
        Collections.shuffle(searchQueries);

        // Generate self credentials
        NodeMeta nodeNodeMeta = new NodeMeta(nodeIp, nodePort, nodeUsername);

        // Initiate the thread for UDP connection
        NodeUDP nodeUDP = new NodeUDP(bootstrapServerNodeMeta, nodeNodeMeta);

        // Register in network
        nodeUDP.register();
        while (true) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (nodeUDP.isRegOk()) {
                for (int i = 0; i < searchQueries.size(); i++) {
                    searchQueryTable.put(i, searchQueries.get(i));
                    Search search = new Search(1, nodeUDP.getNode().getCredential(), searchQueryTable.get(i), 0);
                    nodeUDP.triggerSearchRequest(search);
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                break;
            }
        }

        while (true) ;
    }
}
