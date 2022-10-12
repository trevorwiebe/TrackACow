package com.trevorwiebe.trackacow.data.mapper

import com.trevorwiebe.trackacow.data.entities.RationEntity
import com.trevorwiebe.trackacow.data.cacheEntities.CacheRationEntity
import com.trevorwiebe.trackacow.domain.models.ration.HoldingRationModel
import com.trevorwiebe.trackacow.domain.models.ration.RationModel

fun RationModel.toRationEntity(): RationEntity {
    return RationEntity(
        rationPrimaryKey = rationPrimaryKey,
        rationId = rationId,
        rationName = rationName
    )
}

fun RationEntity.toRationModel(): RationModel {
    return RationModel(
        rationPrimaryKey = rationPrimaryKey,
        rationId = rationId,
        rationName = rationName
    )
}

fun RationModel.toCacheRationModel(whatHappened: Int): HoldingRationModel {
    return HoldingRationModel(
        rationPrimaryKey = rationPrimaryKey,
        rationId = rationId,
        rationName = rationName,
        whatHappened = whatHappened
    )
}

fun CacheRationEntity.toCacheRationModel(): HoldingRationModel {
    return HoldingRationModel(
        rationPrimaryKey = rationPrimaryKey,
        rationId = rationId,
        rationName = rationName,
        whatHappened = whatHappened
    )
}

fun HoldingRationModel.toCacheRationEntity(): CacheRationEntity {
    return CacheRationEntity(
        rationPrimaryKey = rationPrimaryKey,
        rationId = rationId,
        rationName = rationName,
        whatHappened = whatHappened
    )
}