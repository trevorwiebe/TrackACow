package com.trevorwiebe.trackacow.domain.models.lot

data class CacheLotModel(
    var lotPrimaryKey: Int = 0,
    var lotName: String? = "",
    var lotCloudDatabaseId: String = "",
    var customerName: String? = "",
    var notes: String? = "",
    var date: Long = 0,
    var archived: Long = 0,
    var dateArchived: Long? = 0,
    var lotPenCloudDatabaseId: String? = "",
    var whatHappened: Int = 0
)
