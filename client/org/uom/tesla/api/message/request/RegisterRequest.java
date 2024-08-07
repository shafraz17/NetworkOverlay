package org.uom.tesla.api.message.request;

import org.uom.tesla.api.Credential;
import org.uom.tesla.api.message.Message;

public class RegisterRequest extends Message {

    private Credential credential;

    public RegisterRequest(Credential credential) {
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
        message += " " + this.getCredential().getIp() + " " + this.getCredential().getPort() + " " + this.getCredential().getUsername();
        return super.getMessageAsString(message);
    }
}
