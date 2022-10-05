package com.trevorwiebe.trackacow.data.remote.repository

import com.google.firebase.database.FirebaseDatabase
import com.trevorwiebe.trackacow.domain.models.call.CallModel
import com.trevorwiebe.trackacow.domain.repository.remote.CallRepositoryRemote

class CallRepositoryRemoteImpl(
    val firebaseDatabase: FirebaseDatabase,
    private val databasePath: String
): CallRepositoryRemote {

    override suspend fun insertCallRemote(callModel: CallModel) {
        val firebaseId = callModel.primaryKey
        firebaseDatabase.getReference("$databasePath/$firebaseId").setValue(callModel)
    }

    override suspend fun insertCallListRemote(callModelList: List<CallModel>) {
        firebaseDatabase.getReference(databasePath).setValue(callModelList)
    }

}