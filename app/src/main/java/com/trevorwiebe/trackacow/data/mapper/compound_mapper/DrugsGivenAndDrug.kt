package com.trevorwiebe.trackacow.data.mapper.compound_mapper

import com.trevorwiebe.trackacow.data.entities.compound_entities.DrugsGivenAndDrugEntity
import com.trevorwiebe.trackacow.domain.models.compound_model.DrugsGivenAndDrugModel

fun DrugsGivenAndDrugEntity.toDrugsGivenAndDrugModel(): DrugsGivenAndDrugModel{
    return DrugsGivenAndDrugModel(
        drugsGivenPrimaryKey = drugsGivenPrimaryKey,
        drugsGivenId = drugsGivenId,
        drugsGivenDrugId = drugsGivenDrugId,
        drugsGivenAmountGiven = drugsGivenAmountGiven,
        drugsGivenCowId = drugsGivenCowId,
        drugsGivenLotId = drugsGivenLotId,
        drugsGivenDate = drugsGivenDate,
        drugPrimaryKey = drugPrimaryKey,
        defaultAmount = defaultAmount,
        drugCloudDatabaseId = drugCloudDatabaseId,
        drugName = drugName
    )
}

fun DrugsGivenAndDrugModel.toDrugsGivenAndDrugEntity(): DrugsGivenAndDrugEntity{
    return DrugsGivenAndDrugEntity(
        drugsGivenPrimaryKey = drugsGivenPrimaryKey,
        drugsGivenId = drugsGivenId,
        drugsGivenDrugId = drugsGivenDrugId,
        drugsGivenAmountGiven = drugsGivenAmountGiven,
        drugsGivenCowId = drugsGivenCowId,
        drugsGivenLotId = drugsGivenLotId,
        drugsGivenDate = drugsGivenDate,
        drugPrimaryKey = drugPrimaryKey,
        defaultAmount = defaultAmount,
        drugCloudDatabaseId = drugCloudDatabaseId,
        drugName = drugName
    )
}