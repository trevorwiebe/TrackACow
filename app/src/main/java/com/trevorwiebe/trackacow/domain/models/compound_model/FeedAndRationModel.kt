package com.trevorwiebe.trackacow.domain.models.compound_model

data class FeedAndRationModel(
    var feed: Int = 0,
    var date: Long = 0,
    var lotId: String = "",
    var rationCloudId: String? = "",
    var rationName: String? = ""
)
