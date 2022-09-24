package com.trevorwiebe.trackacow.domain.models.call

data class CallModel(
    var primaryKey: Int = 0,
    val callAmount: Int,
    val date: Long,
    val lotId: String,
    val id: String,
)
