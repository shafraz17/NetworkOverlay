package org.uom.tesla.model.response;

import org.uom.tesla.model.NodeMeta;
import org.uom.tesla.model.Message;

import java.util.List;

public class RegisterData extends Message {
    private final int noOfNodes;
    private final List<NodeMeta> nodeMetas;

    public RegisterData(int noOfNodes, List<NodeMeta> nodeMetas) {
        this.noOfNodes = noOfNodes;
        this.nodeMetas = nodeMetas;
    }

    public List<NodeMeta> getCredentials() {
        return nodeMetas;
    }

    public int getNoOfNodes() {
        return noOfNodes;
    }

    @Override
    public String getMessageAsString(String message) {
        message += " " + this.getNoOfNodes();
        StringBuilder messageBuilder = new StringBuilder(message);
        for (NodeMeta node : nodeMetas) {
            messageBuilder.append(" ").append(node.getIp()).append(" ").append(node.getPort());
        }
        message = messageBuilder.toString();
        return super.getMessageAsString(message);
    }
}
