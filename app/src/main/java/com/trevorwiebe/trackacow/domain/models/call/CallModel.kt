package com.trevorwiebe.trackacow.domain.models.call

data class CallModel(
    var callPrimaryKey: Int = 0,
    var callAmount: Int,
    val date: Long,
    val lotId: String,
    val callRationId: Int?,
    var callCloudDatabaseId: String?,
)
