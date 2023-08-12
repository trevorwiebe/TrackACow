package com.trevorwiebe.trackacow.domain.models.feed

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class FeedModel(
    var primaryKey: Int = 0,
    var feed: Int = 0,
    var date: Long = 0,
    var id: String = "",
    var rationCloudId: String? = "",
    var lotId: String = ""
) : Parcelable
