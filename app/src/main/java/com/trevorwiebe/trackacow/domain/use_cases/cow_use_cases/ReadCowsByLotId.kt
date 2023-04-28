package com.trevorwiebe.trackacow.domain.use_cases.cow_use_cases

import com.google.firebase.database.FirebaseDatabase
import com.trevorwiebe.trackacow.domain.models.cow.CowModel
import com.trevorwiebe.trackacow.domain.repository.local.CowRepository
import com.trevorwiebe.trackacow.domain.utils.addQueryListValueEventListenerFlow
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.onStart

class ReadCowsByLotId(
    private val cowRepository: CowRepository,
    private val firebaseDatabase: FirebaseDatabase,
    private val databaseString: String
) {
    @OptIn(FlowPreview::class)
    operator fun invoke(lotId: String): Flow<List<CowModel>> {
        val localFlow = cowRepository.getCowsByLotId(lotId)
        val cowDatabaseRef = firebaseDatabase
            .getReference(databaseString)
            .orderByChild("lotId")
            .equalTo(lotId)
        val cowCloudFlow = cowDatabaseRef.addQueryListValueEventListenerFlow(CowModel::class.java)

        return localFlow
            .flatMapConcat { localData ->
                cowCloudFlow.onStart { emit(localData) }
            }
    }
}
