package org.uom.tesla.model;

public abstract class Message {
    public String getMessageAsString(String message) {
        return String.format("%04d", message.length() + 5) + " " + message;
    }
}
