package com.trevorwiebe.trackacow.data.mapper

import com.trevorwiebe.trackacow.data.cacheEntities.CacheDrugsGivenEntity
import com.trevorwiebe.trackacow.data.entities.DrugsGivenEntity
import com.trevorwiebe.trackacow.domain.models.compound_model.DrugsGivenAndDrugModel
import com.trevorwiebe.trackacow.domain.models.drug_given.CacheDrugGivenModel
import com.trevorwiebe.trackacow.domain.models.drug_given.DrugGivenModel

fun DrugGivenModel.toCacheDrugGivenModel(whatHappened: Int): CacheDrugGivenModel {
    return CacheDrugGivenModel(
        primaryKey = drugsGivenPrimaryKey,
        drugGivenId = drugsGivenId,
        drugId = drugsGivenDrugId,
        amountGiven = drugsGivenAmountGiven,
        cowId = drugsGivenCowId,
        lotId = drugsGivenLotId,
        date = drugsGivenDate,
        whatHappened = whatHappened
    )
}

fun DrugGivenModel.toDrugGivenEntity(): DrugsGivenEntity {
    return DrugsGivenEntity(
        drugsGivenPrimaryKey = drugsGivenPrimaryKey,
        drugsGivenId = drugsGivenId,
        drugsGivenDrugId = drugsGivenDrugId,
        drugsGivenAmountGiven = drugsGivenAmountGiven,
        drugsGivenCowId = drugsGivenCowId,
        drugsGivenLotId = drugsGivenLotId,
        drugsGivenDate = drugsGivenDate,
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

fun CacheDrugGivenModel.toCacheDrugGivenEntity(): CacheDrugsGivenEntity {
    return CacheDrugsGivenEntity(
        primaryKey = primaryKey,
        drugGivenId = drugGivenId,
        drugId = drugId,
        amountGiven = amountGiven,
        cowId = cowId,
        lotId = lotId,
        date = date,
        whatHappened = whatHappened
    )
}