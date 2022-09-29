package com.trevorwiebe.trackacow.domain.models.lot

data class CacheLotModel(
    var primaryKey: Int = 0,
    var lotName: String? = null,
    var lotId: String? = null,
    var customerName: String? = null,
    var notes: String? = null,
    var date: Long = 0,
    var penId: String? = null,
    var whatHappened: Int = 0
)
