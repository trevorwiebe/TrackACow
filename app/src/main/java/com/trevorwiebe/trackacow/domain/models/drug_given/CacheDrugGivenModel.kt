package com.trevorwiebe.trackacow.domain.models.drug_given

data class CacheDrugGivenModel(
    var primaryKey: Int = 0,
    var drugGivenId: String? = "",
    var drugId: String? = "",
    var amountGiven: Int = 0,
    var cowId: String? = "",
    var lotId: String = "",
    var date: Long = 0,
    var whatHappened: Int = 0
)
