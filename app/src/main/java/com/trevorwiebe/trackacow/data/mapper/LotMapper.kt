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
        archived = archived,
        dateArchived = dateArchived,
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
        archived = archived,
        dateArchived = dateArchived,
        lotPenCloudDatabaseId = lotPenCloudDatabaseId
    )
}

fun CacheLotEntity.toCacheLotModel(): CacheLotModel{
    return CacheLotModel(
        lotPrimaryKey = lotPrimaryKey,
        lotName = lotName,
        lotCloudDatabaseId = lotCloudDatabaseId,
        customerName = customerName,
        notes = notes,
        date = date,
        archived = archived,
        dateArchived = dateArchived,
        lotPenCloudDatabaseId = lotPenCloudDatabaseId
    )
}

fun CacheLotModel.toCacheLotEntity(): CacheLotEntity {
    return CacheLotEntity(
        lotPrimaryKey = lotPrimaryKey,
        lotName = lotName,
        lotCloudDatabaseId = lotCloudDatabaseId,
        customerName = customerName,
        notes = notes,
        date = date,
        archived = archived,
        dateArchived = dateArchived,
        lotPenCloudDatabaseId = lotPenCloudDatabaseId
    )
}

fun CacheLotModel.toLotModel(): LotModel {
    return LotModel(
        lotPrimaryKey = lotPrimaryKey,
        lotName = lotName,
        lotCloudDatabaseId = lotCloudDatabaseId,
        customerName = customerName,
        notes = notes,
        date = date,
        archived = archived,
        dateArchived = dateArchived,
        lotPenCloudDatabaseId = lotPenCloudDatabaseId
    )
}

fun LotModel.toCacheLotModel(whatHappened: Int): CacheLotModel {
    return CacheLotModel(
        lotPrimaryKey = lotPrimaryKey,
        lotName = lotName,
        lotCloudDatabaseId = lotCloudDatabaseId,
        customerName = customerName,
        notes = notes,
        date = date,
        archived = archived,
        dateArchived = dateArchived,
        lotPenCloudDatabaseId = lotPenCloudDatabaseId,
        whatHappened = whatHappened
    )
}