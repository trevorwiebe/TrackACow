package com.trevorwiebe.trackacow.domain.use_cases.load_use_cases

import com.trevorwiebe.trackacow.domain.models.load.LoadModel
import com.trevorwiebe.trackacow.domain.repository.local.LoadRepository
import com.trevorwiebe.trackacow.domain.repository.remote.LoadRemoteRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

data class ReadLoadsByLotId(
    private val loadRepository: LoadRepository,
    private val loadRemoteRepository: LoadRemoteRepository
){
    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(lotId: String): Flow<List<LoadModel>> {

        val localFlow = loadRepository.readLoadsByLotId(lotId)
        val cloudLoadFlow = loadRemoteRepository.readLoadsByLotId(lotId)

        return localFlow
            .flatMapLatest { localData ->
                cloudLoadFlow.onStart {
                    emit(localData)
                }.map { loadList ->
                    loadRepository.insertOrUpdateLoadList(loadList)
                    loadList
                }
            }
    }
}
