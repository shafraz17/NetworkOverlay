package org.uom.tesla.api.message.response;

import org.uom.tesla.api.message.Message;

public class UnregisterResponse extends Message {

    private int value;

    public UnregisterResponse(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    @Override
    public String getMessageAsString(String message) {
        message += " " + this.getValue();
        return super.getMessageAsString(message);
    }
}
