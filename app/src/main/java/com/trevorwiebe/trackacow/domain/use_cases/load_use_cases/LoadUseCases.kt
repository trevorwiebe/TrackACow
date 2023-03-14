package com.trevorwiebe.trackacow.domain.use_cases.load_use_cases

data class LoadUseCases(
    val createLoad: CreateLoad,
    val readLoadsByLotId: ReadLoadsByLotId,
    val updateLoad: UpdateLoad,
    val deleteLoad: DeleteLoad
)
