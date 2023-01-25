package com.trevorwiebe.trackacow.domain.use_cases

import com.trevorwiebe.trackacow.domain.models.compound_model.DrugsGivenAndDrugModel

class CalculateDrugsGiven {

    operator fun invoke(drugsGivenAndDrugModelList: List<DrugsGivenAndDrugModel>): List<DrugsGivenAndDrugModel> {

        val list = drugsGivenAndDrugModelList.groupBy { it.drugsGivenDrugId }

        val listToReturn: MutableList<DrugsGivenAndDrugModel> = mutableListOf()
        for (array in list) {
            val drugGivenModel: DrugsGivenAndDrugModel = array.value[0]
            drugGivenModel.drugsGivenAmountGiven = array.value.sumOf { it.drugsGivenAmountGiven }
            listToReturn.add(drugGivenModel)
        }

        return listToReturn.toList()
    }
}