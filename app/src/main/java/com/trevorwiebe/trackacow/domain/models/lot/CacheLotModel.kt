package com.trevorwiebe.trackacow.domain.models.lot

data class CacheLotModel(
    var primaryKey: Int = 0,
    var lotName: String? = null,
    var lotCloudDatabaseId: String? = null,
    var customerName: String? = null,
    var notes: String? = null,
    var date: Long = 0,
    var lotPenCloudDatabaseId: String? = null,
    var whatHappened: Int = 0
)
