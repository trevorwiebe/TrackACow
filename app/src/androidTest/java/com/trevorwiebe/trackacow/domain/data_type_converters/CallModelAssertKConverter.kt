package com.trevorwiebe.trackacow.domain.data_type_converters

import assertk.Assert
import com.trevorwiebe.trackacow.domain.models.call.CacheCallModel
import com.trevorwiebe.trackacow.domain.models.call.CallModel
import com.trevorwiebe.trackacow.domain.models.compound_model.CallAndRationModel

fun Assert<CallModel>.transformCallModel(): Assert<CallModel> {
    return transform {
        CallModel(
            callPrimaryKey = it.callPrimaryKey,
            callAmount = it.callAmount,
            date = it.date,
            lotId = it.lotId,
            callRationId = if (it.callRationId == 0) null else it.callRationId,
            callCloudDatabaseId = it.callCloudDatabaseId
        )
    }
}

fun Assert<CacheCallModel>.transformCacheCallModel(): Assert<CallModel> {
    return transform {
        CallModel(
            callPrimaryKey = it.callPrimaryKey,
            callAmount = it.callAmount,
            date = it.date,
            lotId = it.lotId,
            callRationId = it.callRationId,
            callCloudDatabaseId = it.callCloudDatabaseId
        )
    }
}

fun Assert<CallAndRationModel>.transformCallAndRationModel(): Assert<CallModel> {
    return transform {
        CallModel(
            callPrimaryKey = it.callPrimaryKey,
            callAmount = it.callAmount,
            date = it.date,
            lotId = it.lotId,
            callRationId = if (it.callRationId == 0) null else it.callRationId,
            callCloudDatabaseId = it.callCloudDatabaseId
        )
    }
}