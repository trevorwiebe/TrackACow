package com.trevorwiebe.trackacow.domain.use_cases.drug_use_cases

import com.google.firebase.database.FirebaseDatabase
import com.trevorwiebe.trackacow.domain.models.drug.DrugModel
import com.trevorwiebe.trackacow.domain.repository.local.DrugRepository
import com.trevorwiebe.trackacow.domain.utils.addValueEventListenerFlow
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.onStart

data class ReadDrugsUC(
    private val drugRepository: DrugRepository,
    private val firebaseDatabase: FirebaseDatabase,
    private val databaseString: String
){
    @OptIn(FlowPreview::class)
    operator fun invoke(): Flow<List<DrugModel>> {

        val localFlow = drugRepository.getDrugList()
        val drugDatabaseRef = firebaseDatabase.getReference(databaseString)
        val cloudFlow = drugDatabaseRef.addValueEventListenerFlow(DrugModel::class.java)

        return localFlow
            .flatMapConcat { localData ->
                cloudFlow.onStart { emit(localData) }
            }
    }
}