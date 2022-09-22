package com.trevorwiebe.trackacow.data.mapper

import com.trevorwiebe.trackacow.data.db.entities.RationEntity
import com.trevorwiebe.trackacow.data.db.holdingUpdateEntities.HoldingRationEntity
import com.trevorwiebe.trackacow.domain.models.HoldingRationModel
import com.trevorwiebe.trackacow.domain.models.RationModel

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