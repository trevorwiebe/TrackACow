package com.trevorwiebe.trackacow.domain.use_cases.lot_use_cases

import com.trevorwiebe.trackacow.domain.models.lot.LotModel
import com.trevorwiebe.trackacow.domain.repository.local.LotRepository
import com.trevorwiebe.trackacow.domain.repository.remote.LotRepositoryRemote
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

data class ReadLots(
    private var lotRepository: LotRepository,
    private val lotRepositoryRemote: LotRepositoryRemote,
){
    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(): Flow<List<LotModel>> {

        val localLotFlow = lotRepository.readLots()
        val cloudLotFlow = lotRepositoryRemote.readLots()

        return localLotFlow
            .flatMapLatest { localData ->
                cloudLotFlow.onStart {
                    emit(localData)
                }.map { lotList ->
                    lotRepository.insertOrUpdateLotList(lotList)
                    lotList
                }
            }
    }
}