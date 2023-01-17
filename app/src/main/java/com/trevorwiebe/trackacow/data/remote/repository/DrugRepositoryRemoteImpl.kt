package com.trevorwiebe.trackacow.data.remote.repository

import com.google.firebase.database.FirebaseDatabase
import com.trevorwiebe.trackacow.domain.models.drug.DrugModel
import com.trevorwiebe.trackacow.domain.repository.remote.DrugRepositoryRemote

class DrugRepositoryRemoteImpl(
    val firebaseDatabase: FirebaseDatabase,
    private val databasePath: String
): DrugRepositoryRemote {

    override fun insertDrug(drugModel: DrugModel) {
        if(drugModel.drugCloudDatabaseId.isNotEmpty()) {
            firebaseDatabase.getReference(
                "$databasePath${drugModel.drugCloudDatabaseId}"
            ).setValue(drugModel)
        }
    }

}