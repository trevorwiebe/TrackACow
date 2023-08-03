package com.trevorwiebe.trackacow.domain.use_cases.load_use_cases

import android.app.Application
import com.trevorwiebe.trackacow.domain.models.load.LoadModel
import com.trevorwiebe.trackacow.domain.repository.local.LoadRepository
import com.trevorwiebe.trackacow.domain.repository.remote.LoadRemoteRepository
import com.trevorwiebe.trackacow.domain.utils.Utility
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

data class ReadLoadsByLotId(
        private val loadRepository: LoadRepository,
        private val loadRemoteRepository: LoadRemoteRepository,
        private val context: Application
){

    // TODO: update this with data source identification

    @OptIn(FlowPreview::class)
    operator fun invoke(lotId: String): Flow<List<LoadModel>> {

        val localFlow = loadRepository.readLoadsByLotId(lotId)
        val cloudLoadFlow = loadRemoteRepository.readLoadsByLotId(lotId)

        return if (Utility.haveNetworkConnection(context)) {
            localFlow.flatMapConcat { localData ->
                cloudLoadFlow.onStart {
                    emit(localData)
                }.map { loadList ->
                    loadRepository.insertOrUpdateLoadList(loadList)
                    loadList
                }
            }
        } else {
            localFlow
        }
    }
}
