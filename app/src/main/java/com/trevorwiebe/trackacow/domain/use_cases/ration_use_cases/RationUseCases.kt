package com.trevorwiebe.trackacow.domain.use_cases.ration_use_cases

data class RationUseCases(
    val createRationUC: CreateRationUC,
    val readAllRationsUC: ReadAllRationsUC,
    val updateRationUC: UpdateRationUC,
    val deleteRationByIdUC: DeleteRationByIdUC
)
