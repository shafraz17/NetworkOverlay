package org.uom.tesla.model.payload;

import org.uom.tesla.model.NodeMeta;
import org.uom.tesla.model.Message;

public class Search extends Message {
    private final int sequenceNo;
    private final NodeMeta triggeredNodeMeta;
    private final String fileName;
    private int hops;

    public Search(int sequenceNo, NodeMeta triggeredNodeMeta, String fileName, int hops) {
        this.sequenceNo = sequenceNo;
        this.triggeredNodeMeta = triggeredNodeMeta;
        this.fileName = fileName;
        this.hops = hops;
    }

    public NodeMeta getCredential() {
        return triggeredNodeMeta;
    }

    public int getSequenceNo() {
        return sequenceNo;
    }

    public String getFileName() {
        return fileName;
    }

    public int getHops() {
        return hops;
    }

    public void setHops(int hops) {
        this.hops = hops;
    }

    @Override
    public String getMessageAsString(String message) {
        message += " " + sequenceNo + " " + this.getCredential().getIp() + " " + this.getCredential().getPort() + " " + this.getFileName() + " " + this.getHops();
        return super.getMessageAsString(message);
    }

    public int incHops() {
        return ++hops;
    }
}
