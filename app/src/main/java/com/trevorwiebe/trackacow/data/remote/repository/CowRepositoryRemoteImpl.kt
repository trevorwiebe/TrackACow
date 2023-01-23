package com.trevorwiebe.trackacow.data.remote.repository

import com.google.firebase.database.FirebaseDatabase
import com.trevorwiebe.trackacow.domain.models.cow.CowModel
import com.trevorwiebe.trackacow.domain.repository.remote.CowRepositoryRemote

class CowRepositoryRemoteImpl(
    val firebaseDatabase: FirebaseDatabase,
    private val databasePath: String
) : CowRepositoryRemote {


    override suspend fun insertCowRemote(cowModel: CowModel) {
        if (cowModel.cowId.isNotEmpty()) {
            firebaseDatabase.getReference(
                "$databasePath/${cowModel.cowId}"
            ).setValue(cowModel)
        }
    }

    override suspend fun deleteCowRemote(cowModel: CowModel) {
        if (cowModel.cowId.isNotEmpty()) {
            firebaseDatabase.getReference(
                "$databasePath/${cowModel.cowId}"
            ).removeValue()
        }
    }


}