package org.uom.tesla.api.message.response;

import org.uom.tesla.api.message.Message;

public class ErrorResponse extends Message {

    @Override
    public String getMessageAsString(String message) {
        return super.getMessageAsString(message);
    }

    @Override
    public String toString() {
        String response = "An error occurred";
        return response;
    }
}
