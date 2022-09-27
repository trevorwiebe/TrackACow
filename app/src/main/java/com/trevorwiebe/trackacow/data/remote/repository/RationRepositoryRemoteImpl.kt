package com.trevorwiebe.trackacow.data.remote.repository

import com.google.firebase.database.FirebaseDatabase
import com.trevorwiebe.trackacow.domain.models.ration.RationModel
import com.trevorwiebe.trackacow.domain.repository.remote.RationRepositoryRemote

class RationRepositoryRemoteImpl(
    val firebaseDatabase: FirebaseDatabase,
    private val databasePath: String
): RationRepositoryRemote {

    override suspend fun insertRationRemote(rationModel: RationModel) {
        firebaseDatabase.getReference(databasePath).setValue(rationModel)
    }

    override suspend fun insertRationListRemote(rationModelList: List<RationModel>) {
        firebaseDatabase.getReference(databasePath).setValue(rationModelList)
    }
}