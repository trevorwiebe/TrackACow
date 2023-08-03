package com.trevorwiebe.trackacow.domain.use_cases.cow_use_cases

import android.app.Application
import com.trevorwiebe.trackacow.domain.models.cow.CowModel
import com.trevorwiebe.trackacow.domain.repository.local.CowRepository
import com.trevorwiebe.trackacow.domain.repository.remote.CowRepositoryRemote
import com.trevorwiebe.trackacow.domain.utils.Utility
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

class ReadCowByCowId(
        private val cowRepository: CowRepository,
        private val cowRepositoryRemote: CowRepositoryRemote,
        private val context: Application
) {

    // TODO: update this with data source identification

    @OptIn(FlowPreview::class)
    operator fun invoke(cowId: String): Flow<CowModel?> {
        val localFlow = cowRepository.getCowByCowId(cowId)
        val cowCloudFlow = cowRepositoryRemote.readCowByCowIdRemote(cowId)

        if (Utility.haveNetworkConnection(context)) {
            return localFlow.flatMapConcat { localData ->
                cowCloudFlow.onStart {
                    emit(localData)
                }.map { cowModel ->
                    if (cowModel != null) {
                        val cowList = listOf(cowModel)
                        cowRepository.insertOrUpdateCowList(cowList)
                    }
                    cowModel
                }
            }
        } else {
            return localFlow
        }
    }
}