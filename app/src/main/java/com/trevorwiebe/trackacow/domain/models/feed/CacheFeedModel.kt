package com.trevorwiebe.trackacow.domain.models.feed

data class CacheFeedModel(
    var feed: Int = 0,
    var date: Long = 0,
    var id: String = "",
    var lotId: String = "",
    var rationCloudId: String? = "",
    var whatHappened: Int = 0
)
