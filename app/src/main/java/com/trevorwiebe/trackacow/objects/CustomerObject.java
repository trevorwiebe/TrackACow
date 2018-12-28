package com.trevorwiebe.trackacow.objects;

import android.support.annotation.Keep;

@Keep
public class CustomerObject {

    public static final String CUSTOMER_OBJECT = "customers";

    private String customerName;
    private String customerId;

    public CustomerObject(String customerName, String customerId) {
        this.customerName = customerName;
        this.customerId = customerId;
    }

    public CustomerObject(){}

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }
}
