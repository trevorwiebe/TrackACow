package com.trevorwiebe.trackacow.domain.models.call

data class HoldingCallModel (
    var callPrimaryKey: Int = 0,
    val callAmount: Int,
    val date: Long,
    val lotId: String,
    val callRationId: Int?,
    val callCloudDatabaseId: String?,
    val whatHappened: Int
)