package org.uom.tesla.api.message.response;

import org.uom.tesla.api.Credential;
import org.uom.tesla.api.message.Message;

public class JoinResponse extends Message {

    private int value;

    private Credential senderCredential;

    public JoinResponse(int value, Credential senderCredential) {
        this.value = value;
        this.senderCredential = senderCredential;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public Credential getSenderCredential() {
        return senderCredential;
    }

    public void setSenderCredential(Credential senderCredential) {
        this.senderCredential = senderCredential;
    }

    @Override
    public String getMessageAsString(String message) {
        message += " " + this.getValue();
        return super.getMessageAsString(message);
    }
}
