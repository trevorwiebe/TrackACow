package com.trevorwiebe.trackacow.domain.use_cases.ration_use_cases

import com.google.firebase.database.FirebaseDatabase
import com.trevorwiebe.trackacow.domain.models.ration.RationModel
import com.trevorwiebe.trackacow.domain.repository.local.RationsRepository
import com.trevorwiebe.trackacow.domain.utils.addListValueEventListenerFlow
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.onStart

class ReadAllRationsUC(
    private val rationsRepository: RationsRepository,
    private val firebaseDatabase: FirebaseDatabase,
    private val databaseString: String
) {
    @OptIn(FlowPreview::class)
    operator fun invoke(): Flow<List<RationModel>> {
        val localFlow = rationsRepository.getRations()
        val rationRef = firebaseDatabase.getReference(databaseString)
        val rationCloudFlow = rationRef.addListValueEventListenerFlow(RationModel::class.java)

        // TODO: figure out how to collect the cloud ration flow and save it to the local db

        return localFlow
            .flatMapConcat { localData ->
                rationCloudFlow
                    .onStart { emit(localData) }
            }
    }
}