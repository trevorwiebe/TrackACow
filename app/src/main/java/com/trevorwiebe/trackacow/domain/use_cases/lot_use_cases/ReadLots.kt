package com.trevorwiebe.trackacow.domain.use_cases.lot_use_cases

import com.google.firebase.database.FirebaseDatabase
import com.trevorwiebe.trackacow.domain.models.lot.LotModel
import com.trevorwiebe.trackacow.domain.repository.local.LotRepository
import com.trevorwiebe.trackacow.domain.utils.addQueryListValueEventListenerFlow
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.onStart

data class ReadLots(
    private var lotRepository: LotRepository,
    private val firebaseDatabase: FirebaseDatabase,
    private val lotDatabaseString: String
){
    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(): Flow<List<LotModel>> {

        val localLotFlow = lotRepository.readLots()

        val lotRef = firebaseDatabase
            .getReference(lotDatabaseString)
            .orderByChild("archived")
            .equalTo(0.toDouble())

        val lotCloudFlow = lotRef.addQueryListValueEventListenerFlow(LotModel::class.java)

        return localLotFlow
            .flatMapLatest { localData ->
                lotCloudFlow.onStart { emit(localData) }
            }
    }
}