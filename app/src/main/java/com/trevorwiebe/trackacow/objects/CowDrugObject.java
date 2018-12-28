package com.trevorwiebe.trackacow.objects;

public class CowDrugObject {

    // TODO: 12/27/2018 add date field
    private String drugId;
    private int amountGiven;

    public CowDrugObject(String drugId, int amountGiven) {
        this.drugId = drugId;
        this.amountGiven = amountGiven;
    }

    public CowDrugObject(){}

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
}
