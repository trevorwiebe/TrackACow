package com.trevorwiebe.trackacow.domain.models.cow

data class CacheCowModel(
    var primaryKey: Int = 0,
    var isAlive: Int = 0,
    var cowId: String = "",
    var tagNumber: Int = 0,
    var date: Long = 0,
    var notes: String? = "",
    var lotId: String? = "",
    var whatHappened: Int = 0
)
