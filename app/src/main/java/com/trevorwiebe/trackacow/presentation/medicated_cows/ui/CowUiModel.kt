package com.trevorwiebe.trackacow.presentation.medicated_cows.ui

import com.trevorwiebe.trackacow.domain.models.compound_model.DrugsGivenAndDrugModel
import com.trevorwiebe.trackacow.domain.models.cow.CowModel

data class CowUiModel(
    val cowModel: CowModel,
    val drugsGivenAndDrugModelList: List<DrugsGivenAndDrugModel>
)
