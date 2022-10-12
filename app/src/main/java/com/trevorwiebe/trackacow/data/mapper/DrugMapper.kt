package com.trevorwiebe.trackacow.data.mapper

import com.trevorwiebe.trackacow.data.entities.DrugEntity
import com.trevorwiebe.trackacow.domain.models.drug.DrugModel

fun DrugEntity.toDrugModel(): DrugModel {
    return DrugModel(
        primaryKey = primaryKey,
        defaultAmount = defaultAmount,
        drugId = drugId,
        drugName = drugName
    )
}

fun DrugModel.toDrugEntity(): DrugEntity{
    return DrugEntity(
        primaryKey = primaryKey,
        defaultAmount = defaultAmount,
        drugId = drugId,
        drugName = drugName
    )
}