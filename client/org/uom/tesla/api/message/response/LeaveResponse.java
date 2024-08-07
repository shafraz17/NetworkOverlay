package org.uom.tesla.api.message.response;

import org.uom.tesla.api.message.Message;

public class LeaveResponse extends Message {

    private int value;

    public LeaveResponse(int value) {
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
