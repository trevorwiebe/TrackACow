package com.trevorwiebe.trackacow.data.mapper

import com.trevorwiebe.trackacow.data.entities.CowEntity
import com.trevorwiebe.trackacow.domain.models.cow.CowModel

fun CowEntity.toCowModel(): CowModel{
    return CowModel(
        primaryKey = primaryKey,
        isAlive = isAlive,
        cowId = cowId,
        tagNumber = tagNumber,
        date = date,
        notes = notes,
        lotId = lotId
    )
}

fun CowModel.toCowEntity(): CowEntity{
    return CowEntity(
        primaryKey = primaryKey,
        isAlive = isAlive,
        cowId = cowId,
        tagNumber = tagNumber,
        date = date,
        notes = notes,
        lotId = lotId
    )
}