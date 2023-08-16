package com.trevorwiebe.trackacow.data.entities.compound_entities

data class FeedAndRationEntity(
        var feed: Int = 0,
        var date: Long = 0,
        var lotId: String = "",
        var rationCloudId: String? = "",
        var rationName: String? = ""
)
