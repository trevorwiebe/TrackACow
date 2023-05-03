package com.trevorwiebe.trackacow.domain.models.call

data class CallModel(
    var callPrimaryKey: Int = 0,
    var callAmount: Int = 0,
    var date: Long = 0,
    var lotId: String = "",
    var callRationId: Int? = 0,
    var callCloudDatabaseId: String? = "",
)
