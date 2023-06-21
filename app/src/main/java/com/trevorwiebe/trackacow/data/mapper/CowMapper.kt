package com.trevorwiebe.trackacow.data.mapper

import com.trevorwiebe.trackacow.data.cacheEntities.CacheCowEntity
import com.trevorwiebe.trackacow.data.entities.CowEntity
import com.trevorwiebe.trackacow.domain.models.cow.CacheCowModel
import com.trevorwiebe.trackacow.domain.models.cow.CowModel

fun CowEntity.toCowModel(): CowModel{
    return CowModel(
        primaryKey = primaryKey,
        alive = alive,
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
        alive = alive,
        cowId = cowId,
        tagNumber = tagNumber,
        date = date,
        notes = notes,
        lotId = lotId
    )
}

fun CowModel.toCacheCowModel(whatHappened: Int): CacheCowModel {
    return CacheCowModel(
        primaryKey = primaryKey,
        alive = alive,
        cowId = cowId,
        tagNumber = tagNumber,
        date = date,
        notes = notes,
        lotId = lotId,
        whatHappened = whatHappened
    )
}

fun CacheCowModel.toCacheCowEntity(): CacheCowEntity {
    return CacheCowEntity(
        primaryKey = primaryKey,
        alive = alive,
        cowId = cowId,
        tagNumber = tagNumber,
        date = date,
        notes = notes,
        lotId = lotId,
        whatHappened = whatHappened
    )
}

fun CacheCowModel.toCowModel(): CowModel {
    return CowModel(
        primaryKey = primaryKey,
        alive = alive,
        cowId = cowId,
        tagNumber = tagNumber,
        date = date,
        notes = notes,
        lotId = lotId,
    )
}

fun CacheCowEntity.toCacheCowModel(): CacheCowModel {
    return CacheCowModel(
        primaryKey = primaryKey,
        alive = alive,
        cowId = cowId,
        tagNumber = tagNumber,
        date = date,
        notes = notes,
        lotId = lotId,
        whatHappened = whatHappened
    )
}