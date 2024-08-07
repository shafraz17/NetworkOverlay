package org.uom.tesla.model;

import org.uom.tesla.model.Message;

public class Error extends Message {
    @Override
    public String getMessageAsString(String message) {
        return super.getMessageAsString(message);
    }

    @Override
    public String toString() {
        return "An error occurred";
    }
}
