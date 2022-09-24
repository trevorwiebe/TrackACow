package com.trevorwiebe.trackacow.domain.models.ration

data class HoldingRationModel(
    var primaryKey: Int,
    var rationId: String,
    var rationName: String,
    var whatHappened: Int
)
