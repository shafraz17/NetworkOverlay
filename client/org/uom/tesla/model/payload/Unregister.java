package org.uom.tesla.model.payload;

import org.uom.tesla.model.NodeMeta;
import org.uom.tesla.model.Message;

public class Unregister extends Message {

    private final NodeMeta nodeMeta;

    public Unregister(NodeMeta nodeMeta) {
        this.nodeMeta = nodeMeta;
    }

    public NodeMeta getCredential() {
        return nodeMeta;
    }

    @Override
    public String getMessageAsString(String message) {
        message += " " + this.getCredential().getIp() + " " + this.getCredential().getPort() + " " + this.getCredential().getUsername();
        return super.getMessageAsString(message);
    }
}