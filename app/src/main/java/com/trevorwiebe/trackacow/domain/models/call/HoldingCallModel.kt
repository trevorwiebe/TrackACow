package com.trevorwiebe.trackacow.domain.models.call

data class HoldingCallModel (
    var primaryKey: Int = 0,
    val callAmount: Int,
    val date: Long,
    val lotId: String,
    val callCloudDatabaseId: String?,
    val whatHappened: Int
)