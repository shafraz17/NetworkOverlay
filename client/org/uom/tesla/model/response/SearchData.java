package org.uom.tesla.model.response;

import org.uom.tesla.model.NodeMeta;
import org.uom.tesla.model.Message;

import java.util.List;

public class SearchData extends Message {
    private final int sequenceNo;
    private final int noOfFiles;
    private final NodeMeta nodeMeta;
    private final int hops;
    private final List<String> fileList;

    public SearchData(int sequenceNo, int noOfFiles, NodeMeta nodeMeta, int hops, List<String> fileList) {
        this.sequenceNo = sequenceNo;
        this.noOfFiles = noOfFiles;
        this.nodeMeta = nodeMeta;
        this.hops = hops;
        this.fileList = fileList;
    }

    public int getNoOfFiles() {
        return noOfFiles;
    }

    public NodeMeta getCredential() {
        return nodeMeta;
    }

    public int getSequenceNo() {
        return sequenceNo;
    }

    public int getHops() {
        return hops;
    }

    @Override
    public String getMessageAsString(String message) {
        message += " " + sequenceNo + " " + this.getNoOfFiles() + " " + this.getCredential().getIp() + " " + this.getCredential().getPort() + " " + this.getHops();
        StringBuilder messageBuilder = new StringBuilder(message);
        for (String file : fileList) {
            messageBuilder.append(" ").append(file);
        }
        message = messageBuilder.toString();
        return super.getMessageAsString(message);
    }

    @Override
    public String toString() {
        StringBuilder response = new StringBuilder("Search Results:" +
                "\nSequence No: " + this.getSequenceNo() +
                "\nNo of files: " + fileList.size() +
                "\nIP: " + this.getCredential().getIp() +
                "\nPort: " + this.getCredential().getPort() +
                "\nHop count: " + this.getHops());
        for (int i = 1; i <= fileList.size(); i++) {
            response.append("\nFile ").append(i).append(": ").append(fileList.get(i - 1));
        }

        return response.toString();
    }
}
