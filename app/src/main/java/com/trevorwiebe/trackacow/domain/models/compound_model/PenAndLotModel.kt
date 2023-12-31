package com.trevorwiebe.trackacow.domain.models.compound_model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PenAndLotModel(
    var penPrimaryKey: Int = 0,
    var penCloudDatabaseId: String? = "",
    var penName: String = "",
    var lotPrimaryKey: Int? = 0,
    var lotName: String? = "",
    var lotCloudDatabaseId: String? = "",
    var customerName: String? = null,
    var rationId: String? = null,
    var notes: String? = null,
    var date: Long? = 0,
    var archived: Long = 0,
    var dateArchived: Long = 0
): Parcelable
