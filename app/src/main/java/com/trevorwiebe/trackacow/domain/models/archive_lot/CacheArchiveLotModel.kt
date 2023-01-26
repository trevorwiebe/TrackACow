package com.trevorwiebe.trackacow.domain.models.archive_lot

data class CacheArchiveLotModel(
    var primaryKey: Int = 0,
    var lotName: String? = "",
    var lotId: String? = "",
    var customerName: String? = "",
    var notes: String? = "",
    var dateStarted: Long = 0,
    var dateEnded: Long = 0,
    var whatHappened: Int = 0
)