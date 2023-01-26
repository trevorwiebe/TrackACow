package com.trevorwiebe.trackacow.domain.models.archive_lot

data class ArchiveLotModel(
    var primaryKey: Int = 0,
    var lotName: String? = "",
    var lotId: String? = "",
    var customerName: String? = "",
    var notes: String? = "",
    var dateStarted: Long = 0,
    var dateEnded: Long = 0
)
