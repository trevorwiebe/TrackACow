package com.trevorwiebe.trackacow.domain.models.lot

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class LotModel(
    var primaryKey: Int = 0,
    var lotName: String = "",
    var lotId: String = "",
    var customerName: String? = null,
    var notes: String? = null,
    var date: Long = 0,
    var penId: String = ""
): Parcelable
