package com.trevorwiebe.trackacow.domain.use_cases.cow_use_cases

import com.trevorwiebe.trackacow.domain.models.cow.CowModel
import com.trevorwiebe.trackacow.domain.repository.local.CowRepository
import com.trevorwiebe.trackacow.domain.repository.remote.CowRepositoryRemote
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import javax.inject.Provider

data class ReadDeadCowsByLotId(
    private val cowRepository: CowRepository,
    private val cowRepositoryRemote: CowRepositoryRemote,
){

    // TODO: fix issue where cloud data isn't save locally
    @OptIn(FlowPreview::class)
    operator fun invoke(lotId: String): Flow<List<CowModel>> {
        val localFlow = cowRepository.getDeadCowsByLotId(lotId)
        val cowFlow = cowRepositoryRemote.readCowsByLotId(lotId)
        return localFlow
            .flatMapConcat { localData ->
                cowFlow
                    .map { cowList ->
                        cowList.filter {
                            it.alive == 0
                        }
                    }
                    .onStart { emit(localData) }
            }
    }
}
