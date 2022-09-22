package com.trevorwiebe.trackacow.domain.models

data class HoldingRationModel(
    var primaryKey: Int,
    var rationId: String,
    var rationName: String,
    var whatHappened: Int
)
