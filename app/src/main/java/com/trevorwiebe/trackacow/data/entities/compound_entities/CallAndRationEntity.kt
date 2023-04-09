package com.trevorwiebe.trackacow.data.entities.compound_entities

data class CallAndRationEntity(
        var callPrimaryKey: Int = 0,
        var callAmount: Int,
        var date: Long,
        var lotId: String,
        val callRationId: Int? = null,
        var callCloudDatabaseId: String?,
        var rationPrimaryKey: Int?,
        var rationCloudDatabaseId: String?,
        var rationName: String?
)
