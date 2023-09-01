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

class ReadDrugsGivenAndDrugsByLotIdAndDate(
        private val drugRepository: DrugRepository,
        private val drugsGivenRepository: DrugsGivenRepository,
        private val drugsGivenRepositoryRemote: DrugsGivenRepositoryRemote,
        private val context: Application
) {

    @OptIn(FlowPreview::class)
    operator fun invoke(lotId: String, startDate: Long, endDate: Long): SourceIdentifiedListFlow {

        val localFlow = drugsGivenRepository
            .getDrugsGivenAndDrugsByLotIdAndDate(lotId, startDate, endDate)
            .map { drugGiven -> drugGiven to DataSource.Local }

        // firebase can't filter by more than on variable, so re-using this function
        val cloudDrugGivenFlow = drugsGivenRepositoryRemote
            .readDrugsGivenAndDrugsByLotIdRemote(lotId)
            .map { drugGiven -> drugGiven to DataSource.Cloud }

        val isFetchingFromCloud = Utility.haveNetworkConnection(context)

        val flowResult = if (isFetchingFromCloud) {
            localFlow.flatMapConcat { (localData, fromLocalSource) ->
                cloudDrugGivenFlow.flatMapConcat { (pair, fromCloudSource) ->
                    drugRepository.syncCloudDrugToDatabase(pair.first)
                    drugsGivenRepository.syncCloudDrugsGivenToDatabaseByLotIdAndDate(
                        pair.second, lotId, startDate, endDate
                    )
                    flow {
                        val combinedList =
                            combineDrugList(pair.first, pair.second, startDate, endDate)
                        emit(combinedList to fromCloudSource)
                    }
                }.onStart { emit(localData to fromLocalSource) }
            }
        } else {
            localFlow
        }

        return SourceIdentifiedListFlow(flowResult, isFetchingFromCloud)
    }

    private fun combineDrugList(
        drugList: List<DrugModel>,
        drugGivenList: List<DrugGivenModel>,
        startDate: Long,
        endDate: Long
    ): List<DrugsGivenAndDrugModel> {
        val result = mutableListOf<DrugsGivenAndDrugModel>()

        // filtering cloud flow by date here
        drugGivenList.forEach { drugGivenModel ->
            if (drugGivenModel.drugsGivenDate in startDate..endDate) {
                val drug =
                    drugList.find { it.drugCloudDatabaseId == drugGivenModel.drugsGivenDrugId }
                if (drug != null) {
                    val drugsGivenAndDrugModel = drugGivenModel.toDrugGivenAndDrug(drug)
                    result.add(drugsGivenAndDrugModel)
                }
            }
        }
        return result
    }
}