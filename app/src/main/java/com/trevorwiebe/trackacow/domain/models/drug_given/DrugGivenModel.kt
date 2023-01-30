package com.trevorwiebe.trackacow.domain.models.drug_given

data class DrugGivenModel(
    var drugsGivenPrimaryKey: Int = 0,
    var drugsGivenId: String? = "",
    var drugsGivenDrugId: String? = "",
    var drugsGivenAmountGiven: Int = 0,
    var drugsGivenCowId: String? = "",
    var drugsGivenLotId: String? = "",
    var drugsGivenDate: Long = 0
)
