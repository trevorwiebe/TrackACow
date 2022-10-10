package com.trevorwiebe.trackacow.data.mapper

import com.trevorwiebe.trackacow.data.cacheEntities.CacheLotEntity
import com.trevorwiebe.trackacow.data.entities.LotEntity
import com.trevorwiebe.trackacow.domain.models.lot.CacheLotModel
import com.trevorwiebe.trackacow.domain.models.lot.LotModel

fun LotModel.toLotEntity(): LotEntity {
    return LotEntity(
        lotPrimaryKey = lotPrimaryKey,
        lotName = lotName,
        lotCloudDatabaseId = lotCloudDatabaseId,
        customerName = customerName,
        notes = notes,
        date = date,
        lotPenCloudDatabaseId = lotPenCloudDatabaseId
    )
}

fun LotEntity.toLotModel(): LotModel {
    return LotModel(
        lotPrimaryKey = lotPrimaryKey,
        lotName = lotName,
        lotCloudDatabaseId = lotCloudDatabaseId,
        customerName = customerName,
        notes = notes,
        date = date,
        lotPenCloudDatabaseId = lotPenCloudDatabaseId
    )
}

fun CacheLotEntity.toCacheLotModel(): CacheLotModel{
    return CacheLotModel(
        primaryKey = primaryKey,
        lotName = lotName,
        lotCloudDatabaseId = lotCloudDatabaseId,
        customerName = customerName,
        notes = notes,
        date = date,
        lotPenCloudDatabaseId = lotPenCloudDatabaseId
    )
}

fun CacheLotModel.toCacheLotEntity(): CacheLotEntity {
    return CacheLotEntity(
        primaryKey = primaryKey,
        lotName = lotName,
        lotCloudDatabaseId = lotCloudDatabaseId,
        customerName = customerName,
        notes = notes,
        date = date,
        lotPenCloudDatabaseId = lotPenCloudDatabaseId
    )
}

fun LotModel.toCacheLotModel(whatHappened: Int): CacheLotModel{
    return CacheLotModel(
        primaryKey = lotPrimaryKey,
        lotName = lotName,
        lotCloudDatabaseId = lotCloudDatabaseId,
        customerName = customerName,
        notes = notes,
        date = date,
        lotPenCloudDatabaseId = lotPenCloudDatabaseId,
        whatHappened = whatHappened
    )
}

fun LotEntity.toCacheLotEntity(whatHappened: Int): CacheLotEntity{
    return CacheLotEntity(
        primaryKey = lotPrimaryKey,
        lotName = lotName,
        lotCloudDatabaseId = lotCloudDatabaseId,
        customerName = customerName,
        notes = notes,
        date = date,
        lotPenCloudDatabaseId = lotPenCloudDatabaseId,
        whatHappened = whatHappened
    )
}