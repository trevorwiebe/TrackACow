package com.trevorwiebe.trackacow.data.mapper

import com.trevorwiebe.trackacow.data.db.entities.RationEntity
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