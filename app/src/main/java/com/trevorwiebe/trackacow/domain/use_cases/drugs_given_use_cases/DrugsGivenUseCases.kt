package com.trevorwiebe.trackacow.domain.use_cases.drugs_given_use_cases

data class DrugsGivenUseCases (
    val readDrugsGivenAndDrugsByLotId: ReadDrugsGivenAndDrugsByLotId,
    val readDrugsGivenAndDrugsByLotIdAndDate: ReadDrugsGivenAndDrugsByLotIdAndDate,
    val readDrugsGivenAndDrugsByCowId: ReadDrugsGivenAndDrugsByCowId,
    val deleteDrugsGivenByCowId: DeleteDrugsGivenByCowId,
    val deleteDrugGivenByDrugGivenId: DeleteDrugGivenByDrugGivenId,
    val updateDrugGiven: UpdateDrugGiven
)