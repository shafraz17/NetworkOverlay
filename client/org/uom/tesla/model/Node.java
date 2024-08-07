package org.uom.tesla.model;

import java.util.List;

public class Node {
    private NodeMeta nodeMeta;
    private List<String> fileList;
    private List<NodeMeta> routingTable;
    private List<Log> statTable;

    public NodeMeta getCredential() {
        return nodeMeta;
    }

    public void setCredential(NodeMeta nodeMeta) {
        this.nodeMeta = nodeMeta;
    }

    public List<String> getFileList() {
        return fileList;
    }

    public void setFileList(List<String> fileList) {
        this.fileList = fileList;
    }

    public List<NodeMeta> getRoutingTable() {
        return routingTable;
    }

    public void setRoutingTable(List<NodeMeta> routingTable) {
        this.routingTable = routingTable;
    }

    public List<Log> getStatTable() {
        return statTable;
    }

    public void setStatTable(List<Log> statTable) {
        this.statTable = statTable;
    }
}
