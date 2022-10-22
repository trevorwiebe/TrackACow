package com.trevorwiebe.trackacow.data.entities.compound_entities

data class DrugsGivenAndDrug(
    var drugsGivenPrimaryKey: Int = 0,
    var drugsGivenId: String? = "",
    var drugsGivenDrugId: String? = "",
    var drugsGivenAmountGiven: Int = 0,
    var drugsGivenCowId: String? = "",
    var drugsGivenLotId: String? = "",
    var drugsGivenDate: Long = 0,
    var drugPrimaryKey: Int = 0,
    var defaultAmount: Int = 0,
    var drugCloudDatabaseId: String = "",
    var drugName: String = ""
)