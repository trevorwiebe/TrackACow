package com.trevorwiebe.trackacow.data.mapper

import com.trevorwiebe.trackacow.data.entities.RationEntity
import com.trevorwiebe.trackacow.data.cacheEntities.CacheRationEntity
import com.trevorwiebe.trackacow.domain.models.ration.CacheRationModel
import com.trevorwiebe.trackacow.domain.models.ration.RationModel

fun RationModel.toRationEntity(): RationEntity {
    return RationEntity(
        rationPrimaryKey = rationPrimaryKey,
        rationCloudDatabaseId = rationCloudDatabaseId,
        rationName = rationName
    )
}

fun RationEntity.toRationModel(): RationModel {
    return RationModel(
        rationPrimaryKey = rationPrimaryKey,
        rationCloudDatabaseId = rationCloudDatabaseId,
        rationName = rationName
    )
}

fun RationModel.toCacheRationModel(whatHappened: Int): CacheRationModel {
    return CacheRationModel(
        rationPrimaryKey = rationPrimaryKey,
        rationCloudDatabaseId = rationCloudDatabaseId,
        rationName = rationName,
        whatHappened = whatHappened
    )
}

fun CacheRationEntity.toCacheRationModel(): CacheRationModel {
    return CacheRationModel(
        rationPrimaryKey = rationPrimaryKey,
        rationCloudDatabaseId = rationCloudDatabaseId,
        rationName = rationName,
        whatHappened = whatHappened
    )
}

fun CacheRationModel.toCacheRationEntity(): CacheRationEntity {
    return CacheRationEntity(
        rationPrimaryKey = rationPrimaryKey,
        rationCloudDatabaseId = rationCloudDatabaseId,
        rationName = rationName,
        whatHappened = whatHappened
    )
}

fun CacheRationModel.toRationModel(): RationModel {
    return RationModel(
        rationPrimaryKey = rationPrimaryKey,
        rationCloudDatabaseId = rationCloudDatabaseId,
        rationName = rationName
    )
}