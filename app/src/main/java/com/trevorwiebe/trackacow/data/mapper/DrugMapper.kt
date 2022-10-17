package com.trevorwiebe.trackacow.data.mapper

import com.trevorwiebe.trackacow.data.cacheEntities.CacheDrugEntity
import com.trevorwiebe.trackacow.data.entities.DrugEntity
import com.trevorwiebe.trackacow.domain.models.drug.CacheDrugModel
import com.trevorwiebe.trackacow.domain.models.drug.DrugModel

fun DrugEntity.toDrugModel(): DrugModel {
    return DrugModel(
        primaryKey = primaryKey,
        defaultAmount = defaultAmount,
        drugCloudDatabaseId = drugCloudDatabaseId,
        drugName = drugName
    )
}

fun DrugModel.toDrugEntity(): DrugEntity{
    return DrugEntity(
        primaryKey = primaryKey,
        defaultAmount = defaultAmount,
        drugCloudDatabaseId = drugCloudDatabaseId,
        drugName = drugName
    )
}

fun CacheDrugModel.toCacheDrugEntity(): CacheDrugEntity{
    return CacheDrugEntity(
        primaryKey = primaryKey,
        defaultAmount = defaultAmount,
        drugCloudDatabaseId = drugCloudDatabaseId,
        drugName = drugName,
        whatHappened = whatHappened
    )
}

fun CacheDrugEntity.toCacheDrugModel(): CacheDrugModel{
    return CacheDrugModel(
        primaryKey = primaryKey,
        defaultAmount = defaultAmount,
        drugCloudDatabaseId = drugCloudDatabaseId,
        drugName = drugName,
        whatHappened = whatHappened
    )
}

fun DrugModel.toCacheDrugModel(whatHappened: Int): CacheDrugModel{
    return CacheDrugModel(
        primaryKey = primaryKey,
        defaultAmount = defaultAmount,
        drugCloudDatabaseId = drugCloudDatabaseId,
        drugName = drugName,
        whatHappened = whatHappened
    )
}