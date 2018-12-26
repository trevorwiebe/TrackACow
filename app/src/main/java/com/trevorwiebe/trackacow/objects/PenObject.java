package com.trevorwiebe.trackacow.objects;

import android.support.annotation.Keep;

@Keep
public class PenObject {

    public static final String PEN_OBJECT = "pens";
    public static final String PEN_PEN_ID = "penId";
    public static final String PEN_PEN_NAME = "penName";
    public static final String PEN_CUSTOMER_ID = "customerId";

    private String penId;
    private String penName;
    private String customerId;

    public PenObject(String penId, String penName, String customerId) {
        this.penId = penId;
        this.penName = penName;
        this.customerId = customerId;
    }

    public PenObject(){}

    public String getPenId() {
        return penId;
    }

    public void setPenId(String penId) {
        this.penId = penId;
    }

    public String getPenName() {
        return penName;
    }

    public void setPenName(String penName) {
        this.penName = penName;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }
}
