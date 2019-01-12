package com.trevorwiebe.trackacow.objects;

import android.support.annotation.Keep;

@Keep
public class DrugsGivenObject {

    private String drugId;
    private int amountGiven;
    private long date;

    public DrugsGivenObject(String drugId, int amountGiven, long date) {
        this.drugId = drugId;
        this.amountGiven = amountGiven;
        this.date = date;
    }

    public DrugsGivenObject(){}

    public String getDrugId() {
        return drugId;
    }

    public void setDrugId(String drugId) {
        this.drugId = drugId;
    }

    public int getAmountGiven() {
        return amountGiven;
    }

    public void setAmountGiven(int amountGiven) {
        this.amountGiven = amountGiven;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }
}
