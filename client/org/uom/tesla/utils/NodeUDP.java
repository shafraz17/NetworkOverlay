package org.uom.tesla.utils;

import org.uom.tesla.model.NodeMeta;
import org.uom.tesla.model.Message;
import org.uom.tesla.model.payload.Search;
import org.uom.tesla.model.response.SearchData;

import java.util.List;

public interface NodeUDP {

    void start();

    void register();

    void unRegister();

    void join(NodeMeta neighbourNodeMeta);

    void joinOk(NodeMeta senderNodeMeta);

    void leave(NodeMeta neighbourNodeMeta);

    void leaveOk(NodeMeta senderNodeMeta);

    void search(Search search, NodeMeta sendNodeMeta);

    void searchOk(SearchData searchResponse);

    List<String> createFileList();

    void processResponse(Message response);

    void error(NodeMeta senderNodeMeta);

    boolean isRegOk();

    List<String> checkForFiles(String fileName, List<String> fileList);

    void triggerSearchRequest(Search search);

    void printRoutingTable(List<NodeMeta> routingTable);
}
