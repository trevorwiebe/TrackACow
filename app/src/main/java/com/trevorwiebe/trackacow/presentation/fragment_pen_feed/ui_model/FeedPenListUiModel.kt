package com.trevorwiebe.trackacow.presentation.fragment_pen_feed.ui_model

import android.os.Parcelable
import com.trevorwiebe.trackacow.domain.models.compound_model.CallAndRationModel
import com.trevorwiebe.trackacow.domain.models.feed.FeedModel
import kotlinx.parcelize.Parcelize

@Parcelize
data class FeedPenListUiModel(
    val date: Long,
    val callAndRationModel: CallAndRationModel,
    val feedList: List<FeedModel>
) : Parcelable