package org.uom.tesla.api.message.request;

import org.uom.tesla.api.Credential;
import org.uom.tesla.api.message.Message;

public class JoinRequest extends Message {

    private Credential credential;
    private int sequenceNumber;

    public int getSequenceNumber() {
        return sequenceNumber;
    }

    public void setSequenceNumber(int sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    public JoinRequest(Credential credential, int sequenceNumber) {
        this.credential = credential;
        this.sequenceNumber = sequenceNumber;
    }

    public JoinRequest(Credential credential) {
        this.credential = credential;
    }

    public Credential getCredential() {
        return credential;
    }

    public void setCredential(Credential credential) {
        this.credential = credential;
    }

    @Override
    public String getMessageAsString(String message) {
        message += " " + this.getCredential().getIp() + " " + this.getCredential().getPort();
        return super.getMessageAsString(message);
    }
}
