package com.trevorwiebe.trackacow.domain.models.call

data class CallModel(
    var callPrimaryKey: Int = 0,
    var callAmount: Int,
    var date: Long,
    var lotId: String,
    var callRationId: Int?,
    var callCloudDatabaseId: String?,
)
