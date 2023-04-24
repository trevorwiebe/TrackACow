package com.trevorwiebe.trackacow.domain.use_cases.lot_use_cases

import com.google.firebase.database.FirebaseDatabase
import com.trevorwiebe.trackacow.domain.models.lot.LotModel
import com.trevorwiebe.trackacow.domain.repository.local.LotRepository
import com.trevorwiebe.trackacow.domain.utils.addValueEventListenerFlow
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.onStart

data class ReadLots(
    private var lotRepository: LotRepository,
    private val firebaseDatabase: FirebaseDatabase,
    private val lotDatabaseString: String
){
    @OptIn(FlowPreview::class)
    operator fun invoke(): Flow<List<LotModel>> {

        val localLotFlow = lotRepository.readLots()

        val lotRef = firebaseDatabase.getReference(lotDatabaseString)
        val lotCloudFlow = lotRef.addValueEventListenerFlow(LotModel::class.java)

        return localLotFlow
            .flatMapConcat { localData ->
                lotCloudFlow.onStart { emit(localData) }
            }
    }
}