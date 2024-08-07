package org.uom.tesla.model;

import java.util.Date;

public class Log {
    private String searchQuery;
    private Date triggeredTime;
    private Date deliveryTime;
    private int hopsRequired;
    private NodeMeta servedNode;

    public String getSearchQuery() {
        return searchQuery;
    }

    public void setSearchQuery(String searchQuery) {
        this.searchQuery = searchQuery;
    }

    public Date getTriggeredTime() {
        return triggeredTime;
    }

    public void setTriggeredTime(Date triggeredTime) {
        this.triggeredTime = triggeredTime;
    }

    public Date getDeliveryTime() {
        return deliveryTime;
    }

    public void setDeliveryTime(Date deliveryTime) {
        this.deliveryTime = deliveryTime;
    }

    public int getHopsRequired() {
        return hopsRequired;
    }

    public void setHopsRequired(int hopsRequired) {
        this.hopsRequired = hopsRequired;
    }

    public NodeMeta getServedNode() {
        return servedNode;
    }

    public void setServedNode(NodeMeta servedNode) {
        this.servedNode = servedNode;
    }
}
