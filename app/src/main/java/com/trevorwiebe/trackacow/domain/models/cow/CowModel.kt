package com.trevorwiebe.trackacow.domain.models.cow

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CowModel(
    var primaryKey: Int = 0,
    var alive: Int = 0,
    var cowId: String = "",
    var tagNumber: Int = 0,
    var date: Long = 0,
    var notes: String? = "",
    var lotId: String = ""
) : Parcelable
