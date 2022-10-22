package com.trevorwiebe.trackacow.domain.models.drug

data class CacheDrugModel (
    var drugPrimaryKey: Int = 0,
    var defaultAmount: Int = 0,
    var drugCloudDatabaseId: String = "",
    var drugName: String = "",
    var whatHappened: Int = 0,
)