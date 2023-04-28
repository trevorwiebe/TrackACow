package com.trevorwiebe.trackacow.domain.use_cases.cow_use_cases

import com.google.firebase.database.FirebaseDatabase
import com.trevorwiebe.trackacow.domain.models.cow.CowModel
import com.trevorwiebe.trackacow.domain.repository.local.CowRepository
import com.trevorwiebe.trackacow.domain.utils.addQueryListValueEventListenerFlow
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

data class ReadDeadCowsByLotId(
    private val cowRepository: CowRepository,
    private val firebaseDatabase: FirebaseDatabase,
    private val databaseString: String
){
    @OptIn(FlowPreview::class)
    operator fun invoke(lotId: String): Flow<List<CowModel>> {
        val localFlow = cowRepository.getDeadCowsByLotId(lotId)
        val cowRef = firebaseDatabase
            .getReference(databaseString)
            .orderByChild("lotId")
            .equalTo(lotId)
        val cowFlow = cowRef.addQueryListValueEventListenerFlow(CowModel::class.java)
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
