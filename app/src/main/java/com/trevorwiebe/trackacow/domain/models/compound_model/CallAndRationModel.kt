package com.trevorwiebe.trackacow.domain.models.compound_model

data class CallAndRationModel(
    var callPrimaryKey: Int = 0,
    var callAmount: Int,
    var date: Long,
    var lotId: String,
    val callRationId: Int? = null,
    var callCloudDatabaseId: String?,
    var rationPrimaryKey: Int = 0,
    var rationCloudDatabaseId: String,
    var rationName: String
)
