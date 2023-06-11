package com.trevorwiebe.trackacow.domain.use_cases.cow_use_cases

import com.google.firebase.database.FirebaseDatabase
import com.trevorwiebe.trackacow.domain.models.cow.CowModel
import com.trevorwiebe.trackacow.domain.repository.local.CowRepository
import com.trevorwiebe.trackacow.domain.utils.addSingleValueEventListenerFlow
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.onStart

class ReadCowByCowId(
    private val cowRepository: CowRepository,
    private val firebaseDatabase: FirebaseDatabase,
    private val databaseString: String
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(cowId: String): Flow<CowModel?> {
        val localFlow = cowRepository.getCowByCowId(cowId)
        val cowRef = firebaseDatabase.getReference(
            "$databaseString/$cowId"
        )
        val cowCloudFlow = cowRef.addSingleValueEventListenerFlow(CowModel::class.java)

        return localFlow
            .flatMapLatest { localData ->
                cowCloudFlow.onStart {
                    if (localData != null) {
                        emit(localData)
                    }
                }
            }
    }
}