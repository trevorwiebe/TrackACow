package com.trevorwiebe.trackacow.objects;

public class DrugReportsObject {

    private String drugId;
    private int drugAmount;

    public DrugReportsObject(String drugId, int drugAmount) {
        this.drugAmount = drugAmount;
        this.drugId = drugId;
    }

    public int getDrugAmount() {
        return drugAmount;
    }

    public void setDrugAmount(int drugAmount) {
        this.drugAmount = drugAmount;
    }

    public String getDrugId() {
        return drugId;
    }

    public void setDrugId(String drugId) {
        this.drugId = drugId;
    }

}
