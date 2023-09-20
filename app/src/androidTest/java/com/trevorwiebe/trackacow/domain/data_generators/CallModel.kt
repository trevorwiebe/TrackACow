package com.trevorwiebe.trackacow.domain.data_generators

import com.trevorwiebe.trackacow.domain.models.call.CallModel
import java.util.UUID

fun callModel(): CallModel {
    return CallModel(
        callPrimaryKey = 0,
        callAmount = 50,
        date = System.currentTimeMillis(),
        lotId = UUID.randomUUID().toString(),
        callRationId = null,
        callCloudDatabaseId = UUID.randomUUID().toString()
    )
}