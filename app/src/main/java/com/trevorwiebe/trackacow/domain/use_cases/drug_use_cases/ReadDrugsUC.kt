package com.trevorwiebe.trackacow.domain.use_cases.drug_use_cases

import android.app.Application
import com.trevorwiebe.trackacow.domain.models.drug.DrugModel
import com.trevorwiebe.trackacow.domain.repository.local.DrugRepository
import com.trevorwiebe.trackacow.domain.repository.remote.DrugRepositoryRemote
import com.trevorwiebe.trackacow.domain.utils.Utility
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

data class ReadDrugsUC(
        private val drugRepository: DrugRepository,
        private val drugRepositoryRemote: DrugRepositoryRemote,
        private val context: Application
){
    @OptIn(FlowPreview::class)
    operator fun invoke(): Flow<List<DrugModel>> {

        val localDrugFlow = drugRepository.getDrugList()
        val cloudDrugFlow = drugRepositoryRemote.readDrugList()

        return if (Utility.haveNetworkConnection(context)) {
            localDrugFlow.flatMapConcat { localData ->
                cloudDrugFlow.onStart {
                    emit(localData)
                }.map { drugList ->
                    drugRepository.insertOrUpdateDrugList(drugList)
                    drugList
                }
            }
        } else {
            cloudDrugFlow
        }
    }
}