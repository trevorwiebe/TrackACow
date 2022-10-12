package com.trevorwiebe.trackacow.data.mapper.compound_mapper

import com.trevorwiebe.trackacow.data.entities.compound_entities.CallAndRationEntity
import com.trevorwiebe.trackacow.domain.models.compound_model.CallAndRationModel

fun CallAndRationEntity.toCallAndRationModel(): CallAndRationModel{
    return CallAndRationModel(
        callPrimaryKey = callPrimaryKey,
        callAmount  = callAmount,
        date = date,
        lotId = lotId,
        callRationId = callRationId,
        callCloudDatabaseId = callCloudDatabaseId,
        rationPrimaryKey = rationPrimaryKey,
        rationId = rationId,
        rationName = rationName
    )
}

fun CallAndRationModel.toCallAndRationEntity(): CallAndRationEntity{
    return CallAndRationEntity(
        callPrimaryKey = callPrimaryKey,
        callAmount  = callAmount,
        date = date,
        lotId = lotId,
        callRationId = callRationId,
        callCloudDatabaseId = callCloudDatabaseId,
        rationPrimaryKey = rationPrimaryKey,
        rationId = rationId,
        rationName = rationName
    )
}