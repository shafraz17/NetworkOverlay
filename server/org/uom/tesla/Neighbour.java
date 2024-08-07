package org.uom.tesla;
import java.io.IOException;

class Neighbour {
    private String ip;
    private int port;
    private String username;

    public Neighbour(String ip, int port, String username) throws IOException {
        this.ip = ip;
        this.port = port;
        this.username = username;
    }

    public String getIp() {
        return this.ip;
    }

    public String getUsername() {
        return this.username;
    }

    public int getPort() {
        return this.port;
    }
}
