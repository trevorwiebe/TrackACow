package com.trevorwiebe.trackacow.data.mapper

import com.trevorwiebe.trackacow.data.entities.CallEntity
import com.trevorwiebe.trackacow.data.holdingUpdateEntities.HoldingCallEntity
import com.trevorwiebe.trackacow.domain.models.call.CallModel
import com.trevorwiebe.trackacow.domain.models.call.HoldingCallModel

fun CallModel.toCallEntity(): CallEntity {
    return CallEntity(
        primaryKey = primaryKey,
        callAmount = callAmount,
        date = date,
        lotId = lotId,
        id = id
    )
}

fun CallEntity.toCallModel(): CallModel {
    return CallModel(
        primaryKey = primaryKey,
        callAmount = callAmount,
        date = date,
        lotId = lotId,
        id = id
    )
}

fun HoldingCallEntity.toHoldingCallModel(): HoldingCallModel{
    return HoldingCallModel(
        primaryKey = primaryKey,
        callAmount = callAmount,
        date = date,
        lotId = lotId,
        id = id,
        whatHappened = whatHappened
    )
}

fun HoldingCallModel.toHoldingCallEntity(): HoldingCallEntity {
    return HoldingCallEntity(
        primaryKey = primaryKey,
        callAmount = callAmount,
        date = date,
        lotId = lotId,
        id = id,
        whatHappened = whatHappened
    )
}

fun CallModel.toHoldingCallModel(whatHappened: Int): HoldingCallModel{
    return HoldingCallModel(
        primaryKey = primaryKey,
        callAmount = callAmount,
        date = date,
        lotId = lotId,
        id = id,
        whatHappened = whatHappened
    )
}