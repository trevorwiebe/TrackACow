package com.trevorwiebe.trackacow.domain.use_cases.cow_use_cases

data class CowUseCases(
    val readCowsByLotId: ReadCowsByLotId,
    val readDeadCowsByLotId: ReadDeadCowsByLotId
)
