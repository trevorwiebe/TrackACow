package com.trevorwiebe.trackacow.domain.use_cases.lot_use_cases

import com.google.firebase.database.FirebaseDatabase
import com.trevorwiebe.trackacow.domain.models.lot.LotModel
import com.trevorwiebe.trackacow.domain.repository.local.LotRepository
import com.trevorwiebe.trackacow.domain.utils.addQueryListValueEventListenerFlow
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.onStart

data class ReadArchivedLots(
    private var lotRepository: LotRepository,
    private var firebaseDatabase: FirebaseDatabase,
    private var databaseString: String
) {

    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(): Flow<List<LotModel>> {
        val localFlow = lotRepository.readArchivedLots()
        val archiveLotRef = firebaseDatabase
            .getReference(databaseString)
            .orderByChild("archived")
            .equalTo(1.toDouble())
        val archivedLotFlow = archiveLotRef.addQueryListValueEventListenerFlow(LotModel::class.java)

        return localFlow
            .flatMapLatest { localData ->
                archivedLotFlow.onStart { emit(localData) }
            }
    }
}