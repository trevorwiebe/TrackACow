package com.trevorwiebe.trackacow.domain.models.feed

data class FeedModel(
    var primaryKey: Int = 0,
    var feed: Int = 0,
    var date: Long = 0,
    var id: String = "",
    var lotId: String = ""
)
