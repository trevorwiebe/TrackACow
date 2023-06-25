package com.trevorwiebe.trackacow.domain.use_cases.cow_use_cases

import com.trevorwiebe.trackacow.domain.models.cow.CowModel
import com.trevorwiebe.trackacow.domain.repository.local.CowRepository
import com.trevorwiebe.trackacow.domain.repository.remote.CowRepositoryRemote
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

data class ReadDeadCowsByLotId(
    private val cowRepository: CowRepository,
    private val cowRepositoryRemote: CowRepositoryRemote,
){

    @OptIn(FlowPreview::class)
    operator fun invoke(lotId: String): Flow<List<CowModel>> {
        val localCowFlow = cowRepository.getDeadCowsByLotId(lotId)
        val cloudCowFlow = cowRepositoryRemote.readCowsByLotId(lotId)
        return localCowFlow
            .flatMapConcat { localData ->
                cloudCowFlow.map { cowList ->
                    cowRepository.insertOrUpdateCowList(cowList)
                    cowList.filter {
                        it.alive == 0
                    }
                }.onStart {
                    emit(localData)
                }
            }
    }
}
