package com.trevorwiebe.trackacow.domain.use_cases.drugs_given_use_cases

import android.app.Application
import com.trevorwiebe.trackacow.data.mapper.toDrugGivenAndDrug
import com.trevorwiebe.trackacow.domain.models.compound_model.DrugsGivenAndDrugModel
import com.trevorwiebe.trackacow.domain.models.drug.DrugModel
import com.trevorwiebe.trackacow.domain.models.drug_given.DrugGivenModel
import com.trevorwiebe.trackacow.domain.repository.local.DrugRepository
import com.trevorwiebe.trackacow.domain.repository.local.DrugsGivenRepository
import com.trevorwiebe.trackacow.domain.repository.remote.DrugsGivenRepositoryRemote
import com.trevorwiebe.trackacow.domain.utils.DataSource
import com.trevorwiebe.trackacow.domain.utils.SourceIdentifiedListFlow
import com.trevorwiebe.trackacow.domain.utils.Utility
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

class ReadDrugsGivenAndDrugsByLotId(
        private val drugsGivenRepository: DrugsGivenRepository,
        private val drugRepository: DrugRepository,
        private val drugsGivenRepositoryRemote: DrugsGivenRepositoryRemote,
        private val context: Application
) {

    @OptIn(FlowPreview::class)
    operator fun invoke(lotId: String): SourceIdentifiedListFlow {
        val localDrugsGivenFlow = drugsGivenRepository.getDrugsGivenAndDrugs(lotId)
            .map { drugGiven -> drugGiven to DataSource.Local }
        val cloudDrugsGivenFlow = drugsGivenRepositoryRemote
            .readDrugsGivenAndDrugsByLotIdRemote(lotId)
            .map { drugGiven -> drugGiven to DataSource.Cloud }

        val isFetchingFromCloud = Utility.haveNetworkConnection(context)

        val flowResult = if (isFetchingFromCloud) {
            localDrugsGivenFlow.flatMapConcat { (localData, source) ->
                cloudDrugsGivenFlow.flatMapConcat { (pair, source) ->
                    drugRepository.insertOrUpdateDrugList(pair.first)
                    drugsGivenRepository.insertOrUpdateDrugGivenList(pair.second)
                    flow {
                        val combinedList = combineDrugList(pair.first, pair.second)
                        emit(combinedList to source)
                    }
                }.onStart {
                    emit(localData to source)
                }
            }
        } else {
            localDrugsGivenFlow
        }

        return SourceIdentifiedListFlow(flowResult, isFetchingFromCloud)
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