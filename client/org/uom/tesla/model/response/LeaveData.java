package org.uom.tesla.model.response;

import org.uom.tesla.model.Message;

public class LeaveData extends Message {
    private final int value;

    public LeaveData(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    @Override
    public String getMessageAsString(String message) {
        message += " " + this.getValue();
        return super.getMessageAsString(message);
    }
}
