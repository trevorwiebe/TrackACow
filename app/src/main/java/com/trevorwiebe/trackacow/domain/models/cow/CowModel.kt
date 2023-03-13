package com.trevorwiebe.trackacow.domain.models.cow

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CowModel(
    var primaryKey: Int = 0,
    var isAlive: Int = 0,
    var cowId: String = "",
    var tagNumber: Int = 0,
    var date: Long = 0,
    var notes: String? = "",
    var lotId: String = ""
) : Parcelable
