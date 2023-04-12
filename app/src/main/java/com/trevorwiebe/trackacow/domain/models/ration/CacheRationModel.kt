package com.trevorwiebe.trackacow.domain.models.ration

data class CacheRationModel(
        var rationPrimaryKey: Int,
        var rationCloudDatabaseId: String,
        var rationName: String,
        var whatHappened: Int
)
