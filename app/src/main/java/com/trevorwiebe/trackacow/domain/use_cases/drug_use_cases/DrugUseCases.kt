package com.trevorwiebe.trackacow.domain.use_cases.drug_use_cases

data class DrugUseCases (
    val readDrugsUC: ReadDrugsUC,
    val createDrug: CreateDrug,
    val updateDrug: UpdateDrug
)