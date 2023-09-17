package com.trevorwiebe.trackacow.domain.models.compound_model

import java.util.UUID

fun drugsGivenAndDrugsModel(): DrugsGivenAndDrugModel {
    return DrugsGivenAndDrugModel(
        drugsGivenPrimaryKey = 0,
        drugsGivenId = UUID.randomUUID().toString(),
        drugsGivenDrugId = UUID.randomUUID().toString(),
        drugsGivenAmountGiven = 2,
        drugsGivenCowId = null,
        drugsGivenLotId = UUID.randomUUID().toString(),
        drugsGivenDate = 0L,
        drugPrimaryKey = 0,
        defaultAmount = 0,
        drugCloudDatabaseId = UUID.randomUUID().toString(),
        drugName = "drug_name"
    )
}