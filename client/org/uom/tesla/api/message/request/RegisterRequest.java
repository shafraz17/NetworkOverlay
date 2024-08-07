package org.uom.tesla.api.message.request;

import org.uom.tesla.api.Credential;
import org.uom.tesla.api.message.Message;

import java.util.Random;

public class RegisterRequest extends Message {

    private Credential credential;
    private int sequenceNumber;

//    public RegisterRequest(Credential credential) {
//        this.credential = credential;
//    }
    public RegisterRequest(Credential credential) {
        this.credential = credential;
        this.sequenceNumber = generateSequenceNumber();  // Generate a unique sequence number
    }
    public Credential getCredential() {
        return credential;
    }

    public void setCredential(Credential credential) {
        this.credential = credential;
    }


    public void setSequenceNumber(int sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    private int generateSequenceNumber() {
        // Generate a unique sequence number, possibly using a static counter or UUID
        return new Random().nextInt(Integer.MAX_VALUE);
    }

    public int getSequenceNumber() {
        return generateSequenceNumber();
    }

    @Override
    public String getMessageAsString(String message) {
        message += " " + this.getCredential().getIp() + " " + this.getCredential().getPort() + " " + this.getCredential().getUsername();
        return super.getMessageAsString(message);
    }
}
