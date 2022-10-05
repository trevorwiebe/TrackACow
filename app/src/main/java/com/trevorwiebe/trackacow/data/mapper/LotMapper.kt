package com.trevorwiebe.trackacow.data.mapper

import com.trevorwiebe.trackacow.data.cacheEntities.CacheLotEntity
import com.trevorwiebe.trackacow.data.entities.LotEntity
import com.trevorwiebe.trackacow.domain.models.lot.CacheLotModel
import com.trevorwiebe.trackacow.domain.models.lot.LotModel

fun LotModel.toLotEntity(): LotEntity {
    return LotEntity(
        primaryKey = primaryKey,
        lotName = lotName,
        lotId = lotId,
        customerName = customerName,
        notes = notes,
        date = date,
        lotPenId = penId
    )
}

fun LotEntity.toLotModel(): LotModel {
    return LotModel(
        primaryKey = primaryKey,
        lotName = lotName,
        lotId = lotId,
        customerName = customerName,
        notes = notes,
        date = date,
        penId = lotPenId
    )
}

fun CacheLotEntity.toCacheLotModel(): CacheLotModel{
    return CacheLotModel(
        primaryKey = primaryKey,
        lotName = lotName,
        lotId = lotId,
        customerName = customerName,
        notes = notes,
        date = date,
        penId = penId
    )
}

fun CacheLotModel.toCacheLotEntity(): CacheLotEntity {
    return CacheLotEntity(
        primaryKey = primaryKey,
        lotName = lotName,
        lotId = lotId,
        customerName = customerName,
        notes = notes,
        date = date,
        penId = penId
    )
}

fun LotModel.toCacheLotModel(whatHappened: Int): CacheLotModel{
    return CacheLotModel(
        primaryKey = primaryKey,
        lotName = lotName,
        lotId = lotId,
        customerName = customerName,
        notes = notes,
        date = date,
        penId = penId,
        whatHappened = whatHappened
    )
}

fun LotEntity.toCacheLotEntity(whatHappened: Int): CacheLotEntity{
    return CacheLotEntity(
        primaryKey = primaryKey,
        lotName = lotName,
        lotId = lotId,
        customerName = customerName,
        notes = notes,
        date = date,
        penId = lotPenId,
        whatHappened = whatHappened
    )
}