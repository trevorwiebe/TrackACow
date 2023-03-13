package com.trevorwiebe.trackacow.domain.use_cases.cow_use_cases

data class CowUseCases(
    val createCow: CreateCow,
    val readCowsByLotId: ReadCowsByLotId,
    val readDeadCowsByLotId: ReadDeadCowsByLotId,
    val updateCow: UpdateCow,
    val deleteCow: DeleteCow
)
