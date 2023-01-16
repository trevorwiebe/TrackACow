package com.trevorwiebe.trackacow.data.remote.repository

import com.google.firebase.database.FirebaseDatabase
import com.trevorwiebe.trackacow.domain.models.load.LoadModel
import com.trevorwiebe.trackacow.domain.repository.remote.LoadRemoteRepository

class LoadRemoteRepositoryImpl(
    val firebaseDatabase: FirebaseDatabase,
    private val databasePath: String
) : LoadRemoteRepository {


    override fun deleteRemoteLoad(loadModel: LoadModel) {
        if (!loadModel.loadId.isNullOrEmpty()) {
            firebaseDatabase.getReference(
                "$databasePath/${loadModel.loadId}"
            ).removeValue()
        }
    }

}