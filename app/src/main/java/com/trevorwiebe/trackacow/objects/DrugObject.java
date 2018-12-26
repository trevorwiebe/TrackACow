package com.trevorwiebe.trackacow.objects;

import android.support.annotation.Keep;

@Keep
public class DrugObject {

    private String drugId;
    private String drugName;

    public DrugObject(String drugId, String drugName) {
        this.drugId = drugId;
        this.drugName = drugName;
    }

    public DrugObject(){}

    public String getDrugId() {
        return drugId;
    }

    public void setDrugId(String drugId) {
        this.drugId = drugId;
    }

    public String getDrugName() {
        return drugName;
    }

    public void setDrugName(String drugName) {
        this.drugName = drugName;
    }
}
