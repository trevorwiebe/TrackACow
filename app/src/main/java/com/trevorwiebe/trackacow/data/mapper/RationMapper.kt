package com.trevorwiebe.trackacow.data.mapper

import com.trevorwiebe.trackacow.data.local.entities.RationEntity
import com.trevorwiebe.trackacow.data.local.holdingUpdateEntities.HoldingRationEntity
import com.trevorwiebe.trackacow.domain.models.ration.HoldingRationModel
import com.trevorwiebe.trackacow.domain.models.ration.RationModel

fun RationModel.toRationEntity(): RationEntity {
    return RationEntity(
        primaryKey = primaryKey,
        rationId = rationId,
        rationName = rationName
    )
}

fun RationEntity.toRationModel(): RationModel {
    return RationModel(
        primaryKey = primaryKey,
        rationId = rationId,
        rationName = rationName
    )
}

fun RationModel.toHoldingRationModel(whatHappened: Int): HoldingRationModel {
    return HoldingRationModel(
        primaryKey = primaryKey,
        rationId = rationId,
        rationName = rationName,
        whatHappened = whatHappened
    )
}

fun HoldingRationEntity.toHoldingRationModel(): HoldingRationModel {
    return HoldingRationModel(
        primaryKey = primaryKey,
        rationId = rationId,
        rationName = rationName,
        whatHappened = whatHappened
    )
}

fun HoldingRationModel.toHoldingRationEntity(): HoldingRationEntity {
    return HoldingRationEntity(
        primaryKey = primaryKey,
        rationId = rationId,
        rationName = rationName,
        whatHappened = whatHappened
    )
}