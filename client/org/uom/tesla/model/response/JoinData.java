package org.uom.tesla.model.response;

import org.uom.tesla.model.NodeMeta;
import org.uom.tesla.model.Message;

public class JoinData extends Message {
    private final int value;
    private final NodeMeta senderNodeMeta;

    public JoinData(int value, NodeMeta senderNodeMeta) {
        this.value = value;
        this.senderNodeMeta = senderNodeMeta;
    }

    public int getValue() {
        return value;
    }

    public NodeMeta getSenderCredential() {
        return senderNodeMeta;
    }

    @Override
    public String getMessageAsString(String message) {
        message += " " + this.getValue();
        return super.getMessageAsString(message);
    }
}
