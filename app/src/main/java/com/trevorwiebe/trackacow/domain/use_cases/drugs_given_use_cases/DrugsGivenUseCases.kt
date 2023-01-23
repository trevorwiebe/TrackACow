package com.trevorwiebe.trackacow.domain.use_cases.drugs_given_use_cases

data class DrugsGivenUseCases (
    val readDrugsGivenAndDrugsByLotId: ReadDrugsGivenAndDrugsByLotId,
    val deleteDrugsGivenByCowId: DeleteDrugsGivenByCowId
)