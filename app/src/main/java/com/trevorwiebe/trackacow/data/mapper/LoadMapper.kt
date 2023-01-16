package com.trevorwiebe.trackacow.data.mapper

import com.trevorwiebe.trackacow.data.cacheEntities.CacheLoadEntity
import com.trevorwiebe.trackacow.data.entities.LoadEntity
import com.trevorwiebe.trackacow.domain.models.load.CacheLoadModel
import com.trevorwiebe.trackacow.domain.models.load.LoadModel

fun LoadEntity.toLoadModel(): LoadModel{
    return LoadModel(
        primaryKey = primaryKey,
        numberOfHead = numberOfHead,
        date = date,
        description = description,
        lotId = lotId,
        loadId = loadId
    )
}

fun LoadModel.toLoadEntity(): LoadEntity {
    return LoadEntity(
        primaryKey = primaryKey,
        numberOfHead = numberOfHead,
        date = date,
        description = description,
        lotId = lotId,
        loadId = loadId
    )
}

fun LoadModel.toCacheLoadModel(whatHappened: Int): CacheLoadModel {
    return CacheLoadModel(
        primaryKey = primaryKey,
        numberOfHead = numberOfHead,
        date = date,
        description = description,
        lotId = lotId,
        loadId = loadId,
        whatHappened = whatHappened
    )
}

fun CacheLoadModel.toCacheLoadEntity(): CacheLoadEntity {
    return CacheLoadEntity(
        primaryKey = primaryKey,
        numberOfHead = numberOfHead,
        date = date,
        description = description,
        lotId = lotId,
        loadId = loadId,
        whatHappened = whatHappened
    )
}