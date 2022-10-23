package com.trevorwiebe.trackacow.data.mapper

import com.trevorwiebe.trackacow.data.entities.LoadEntity
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

fun LoadModel.toLoadEntity(): LoadEntity{
    return LoadEntity(
        primaryKey = primaryKey,
        numberOfHead = numberOfHead,
        date = date,
        description = description,
        lotId = lotId,
        loadId = loadId
    )
}