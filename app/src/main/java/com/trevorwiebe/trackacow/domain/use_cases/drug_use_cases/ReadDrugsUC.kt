package com.trevorwiebe.trackacow.domain.use_cases.drug_use_cases

import android.app.Application
import com.trevorwiebe.trackacow.domain.repository.local.DrugRepository
import com.trevorwiebe.trackacow.domain.repository.remote.DrugRepositoryRemote
import com.trevorwiebe.trackacow.domain.utils.DataSource
import com.trevorwiebe.trackacow.domain.utils.SourceIdentifiedListFlow
import com.trevorwiebe.trackacow.domain.utils.Utility
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

data class ReadDrugsUC(
        private val drugRepository: DrugRepository,
        private val drugRepositoryRemote: DrugRepositoryRemote,
        private val context: Application
){
    @OptIn(FlowPreview::class)
    operator fun invoke(): SourceIdentifiedListFlow {

        val localDrugFlow = drugRepository.getDrugList()
            .map { drugList -> drugList to DataSource.Local }
        val cloudDrugFlow = drugRepositoryRemote.readDrugList()
            .map { drugList -> drugList to DataSource.Cloud }

        val isFetchingFromCloud = Utility.haveNetworkConnection(context)

        val resultFlow = if (isFetchingFromCloud) {
            localDrugFlow.flatMapConcat { (localData, fromLocalSource) ->
                cloudDrugFlow.onStart {
                    emit(localData to fromLocalSource)
                }.map { (drugList, fromCloudSource) ->
                    if (fromCloudSource == DataSource.Cloud) {
                        drugRepository.syncCloudDrugToDatabase(drugList)
                    }
                    drugList to fromCloudSource
                }
            }
        } else {
            localDrugFlow
        }

        return SourceIdentifiedListFlow(resultFlow, isFetchingFromCloud)

    }
}