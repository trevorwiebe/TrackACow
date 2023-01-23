package com.trevorwiebe.trackacow.presentation.medicated_cows.ui

import android.os.Parcelable
import com.trevorwiebe.trackacow.domain.models.compound_model.DrugsGivenAndDrugModel
import com.trevorwiebe.trackacow.domain.models.cow.CowModel
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CowUiModel(
    val cowModel: CowModel,
    val drugsGivenAndDrugModelList: List<DrugsGivenAndDrugModel>
) : Parcelable
