package com.trevorwiebe.trackacow.domain.use_cases.drugs_given_use_cases

import android.app.Application
import com.trevorwiebe.trackacow.data.mapper.toDrugGivenAndDrug
import com.trevorwiebe.trackacow.domain.models.compound_model.DrugsGivenAndDrugModel
import com.trevorwiebe.trackacow.domain.models.drug.DrugModel
import com.trevorwiebe.trackacow.domain.models.drug_given.DrugGivenModel
import com.trevorwiebe.trackacow.domain.repository.local.DrugRepository
import com.trevorwiebe.trackacow.domain.repository.local.DrugsGivenRepository
import com.trevorwiebe.trackacow.domain.repository.remote.DrugsGivenRepositoryRemote
import com.trevorwiebe.trackacow.domain.utils.Utility
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart

class ReadDrugsGivenAndDrugsByCowId(
        private val drugsGivenRepository: DrugsGivenRepository,
        private val drugRepository: DrugRepository,
        private val drugsGivenRepositoryRemote: DrugsGivenRepositoryRemote,
        private val context: Application
) {

    // TODO: update this with data source identification

    @OptIn(FlowPreview::class)
    operator fun invoke(cowIdList: List<String>): Flow<List<DrugsGivenAndDrugModel>> {

        val localDrugFlow = drugsGivenRepository.getDrugsGivenAndDrugsByCowId(cowIdList)
        val cloudDrugFlow =
            drugsGivenRepositoryRemote.readDrugsGivenAndDrugsByCowIdRemote(cowIdList[0])

        return if (Utility.haveNetworkConnection(context)) {
            localDrugFlow.flatMapConcat { localData ->
                cloudDrugFlow.flatMapConcat { pair ->
                    drugRepository.insertOrUpdateDrugList(pair.first)
                    drugsGivenRepository.insertOrUpdateDrugGivenList(pair.second)
                    flow {
                        val combinedList = combineDrugList(pair.first, pair.second)
                        emit(combinedList)
                    }
                }.onStart { emit(localData) }
            }
        } else {
            localDrugFlow
        }

    }

    private fun combineDrugList(
        drugList: List<DrugModel>,
        drugGivenList: List<DrugGivenModel>
    ): List<DrugsGivenAndDrugModel> {
        val result = mutableListOf<DrugsGivenAndDrugModel>()
        drugGivenList.forEach { drugGivenModel ->
            val drug = drugList.find { it.drugCloudDatabaseId == drugGivenModel.drugsGivenDrugId }
            if (drug != null) {
                val drugsGivenAndDrugModel = drugGivenModel.toDrugGivenAndDrug(drug)
                result.add(drugsGivenAndDrugModel)
            }
        }
        return result
    }
}