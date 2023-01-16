package com.trevorwiebe.trackacow.domain.models.load

data class CacheLoadModel(
    var primaryKey: Int = 0,
    var numberOfHead: Int = 0,
    var date: Long = 0,
    var description: String? = "",
    var lotId: String? = "",
    var loadId: String? = "",
    var whatHappened: Int = 0
)
