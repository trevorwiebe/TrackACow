package com.trevorwiebe.trackacow.data.mapper

import com.trevorwiebe.trackacow.data.entities.CallEntity
import com.trevorwiebe.trackacow.data.cacheEntities.CacheCallEntity
import com.trevorwiebe.trackacow.domain.models.call.CallModel
import com.trevorwiebe.trackacow.domain.models.call.CacheCallModel
import com.trevorwiebe.trackacow.domain.models.compound_model.CallAndRationModel
import com.trevorwiebe.trackacow.domain.models.ration.RationModel

fun CallModel.toCallEntity(): CallEntity {
    return CallEntity(
        callPrimaryKey = callPrimaryKey,
        callAmount = callAmount,
        date = date,
        lotId = lotId,
        callRationId = callRationId,
        callCloudDatabaseId = callCloudDatabaseId
    )
}

fun CallModel.toCallAndRationModel(rationModel: RationModel?): CallAndRationModel {
    return CallAndRationModel(
        callPrimaryKey = callPrimaryKey,
        callAmount = callAmount,
        date = date,
        lotId = lotId,
        callRationId = callRationId,
        callCloudDatabaseId = callCloudDatabaseId,
        rationPrimaryKey = rationModel?.rationPrimaryKey,
        rationCloudDatabaseId = rationModel?.rationCloudDatabaseId,
        rationName = rationModel?.rationName
    )
}

fun CallEntity.toCallModel(): CallModel {
    return CallModel(
        callPrimaryKey = callPrimaryKey,
        callAmount = callAmount,
        date = date,
        lotId = lotId,
        callRationId = callRationId,
        callCloudDatabaseId = callCloudDatabaseId
    )
}

fun CacheCallModel.toHoldingCallEntity(): CacheCallEntity {
    return CacheCallEntity(
        callPrimaryKey = callPrimaryKey,
        callAmount = callAmount,
        date = date,
        lotId = lotId,
        callRationId = callRationId,
        callCloudDatabaseId = callCloudDatabaseId,
        whatHappened = whatHappened
    )
}

fun CallModel.toHoldingCallModel(whatHappened: Int): CacheCallModel{
    return CacheCallModel(
        callPrimaryKey = callPrimaryKey,
        callAmount = callAmount,
        date = date,
        lotId = lotId,
        callRationId = callRationId,
        callCloudDatabaseId = callCloudDatabaseId,
        whatHappened = whatHappened
    )
}

fun CacheCallModel.toCallModel(): CallModel {
    return CallModel(
        callPrimaryKey = callPrimaryKey,
        callAmount = callAmount,
        date = date,
        lotId = lotId,
        callRationId = callRationId,
        callCloudDatabaseId = callCloudDatabaseId
    )
}

fun CacheCallEntity.toCacheCallModel(): CacheCallModel {
    return CacheCallModel(
        callPrimaryKey = callPrimaryKey,
        callAmount = callAmount,
        date = date,
        lotId = lotId,
        callRationId = callRationId,
        callCloudDatabaseId = callCloudDatabaseId,
        whatHappened = whatHappened
    )
}