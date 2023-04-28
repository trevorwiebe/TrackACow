package com.trevorwiebe.trackacow.data.mapper.compound_mapper

import com.trevorwiebe.trackacow.data.entities.compound_entities.DrugsGivenAndDrugEntity
import com.trevorwiebe.trackacow.domain.models.compound_model.DrugsGivenAndDrugModel
import com.trevorwiebe.trackacow.domain.models.drug_given.DrugGivenModel

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

fun DrugsGivenAndDrugModel.toDrugGivenModel(): DrugGivenModel {
    return DrugGivenModel(
        drugsGivenPrimaryKey = drugsGivenPrimaryKey,
        drugsGivenId = drugsGivenId,
        drugsGivenDrugId = drugsGivenDrugId,
        drugsGivenAmountGiven = drugsGivenAmountGiven,
        drugsGivenCowId = drugsGivenCowId,
        drugsGivenLotId = drugsGivenLotId,
        drugsGivenDate = drugsGivenDate
    )
}