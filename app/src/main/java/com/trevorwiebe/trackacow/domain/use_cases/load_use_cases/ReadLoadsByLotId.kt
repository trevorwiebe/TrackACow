package com.trevorwiebe.trackacow.domain.use_cases.load_use_cases

import com.google.firebase.database.FirebaseDatabase
import com.trevorwiebe.trackacow.domain.models.load.LoadModel
import com.trevorwiebe.trackacow.domain.repository.local.LoadRepository
import com.trevorwiebe.trackacow.domain.utils.addQueryListValueEventListenerFlow
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.onStart

data class ReadLoadsByLotId(
    private val loadRepository: LoadRepository,
    private val firebaseDatabase: FirebaseDatabase,
    private val databaseString: String
){
    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(lotId: String): Flow<List<LoadModel>> {
        val localFlow = loadRepository.readLoadsByLotId(lotId)
        val loadRef = firebaseDatabase
            .getReference(databaseString)
            .orderByChild("lotId")
            .equalTo(lotId)
        val loadFlow = loadRef.addQueryListValueEventListenerFlow(LoadModel::class.java)

        return localFlow
            .flatMapLatest { localData ->
                loadFlow.onStart { emit(localData) }
            }
    }
}
