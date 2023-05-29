package com.trevorwiebe.trackacow.domain.use_cases.lot_use_cases

import com.google.firebase.database.FirebaseDatabase
import com.trevorwiebe.trackacow.domain.models.lot.LotModel
import com.trevorwiebe.trackacow.domain.repository.local.LotRepository
import com.trevorwiebe.trackacow.domain.utils.addSingleValueEventListenerFlow
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.onStart

data class ReadLotsByLotId(
    private val lotRepository: LotRepository,
    private val firebaseDatabase: FirebaseDatabase,
    private val lotDatabaseString: String
) {

    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(lotCloudDatabaseId: String): Flow<LotModel?> {
        val localFlow = lotRepository.readLotByLotId(lotCloudDatabaseId)

        val lotRef = firebaseDatabase.getReference(
            "$lotDatabaseString/$lotCloudDatabaseId"
        )
        val lotCloudFlow = lotRef.addSingleValueEventListenerFlow(LotModel::class.java)

        return lotCloudFlow
            .flatMapLatest { cloudData ->
                lotRepository.insertOrUpdateLotList(listOf(cloudData))
                localFlow.onStart {
                    emit(cloudData)
                }
            }
    }
}