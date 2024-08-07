package org.uom.tesla.model.payload;

import org.uom.tesla.model.NodeMeta;
import org.uom.tesla.model.Message;

public class Leave extends Message {

    private final NodeMeta nodeMeta;

    public Leave(NodeMeta nodeMeta) {
        this.nodeMeta = nodeMeta;
    }

    public NodeMeta getCredential() {
        return nodeMeta;
    }

    @Override
    public String getMessageAsString(String message) {
        message += " " + this.getCredential().getIp() + " " + this.getCredential().getPort();
        return super.getMessageAsString(message);
    }
}
