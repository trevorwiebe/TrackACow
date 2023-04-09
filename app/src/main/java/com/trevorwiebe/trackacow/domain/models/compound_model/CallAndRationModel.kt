package com.trevorwiebe.trackacow.domain.models.compound_model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CallAndRationModel(
        var callPrimaryKey: Int = 0,
        var callAmount: Int,
        var date: Long,
        var lotId: String,
        val callRationId: Int?,
        var callCloudDatabaseId: String?,
        var rationPrimaryKey: Int?,
        var rationCloudDatabaseId: String?,
        var rationName: String?
) : Parcelable
