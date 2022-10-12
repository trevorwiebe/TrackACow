package com.trevorwiebe.trackacow.domain.models.drug

data class CacheDrugModel (
    var primaryKey: Int = 0,
    var defaultAmount: Int = 0,
    var drugId: String = "",
    var drugName: String = "",
    var whatHappened: Int = 0,
)