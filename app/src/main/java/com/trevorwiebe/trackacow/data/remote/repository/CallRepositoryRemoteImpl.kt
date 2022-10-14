package com.trevorwiebe.trackacow.data.remote.repository

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.trevorwiebe.trackacow.domain.models.call.CallModel
import com.trevorwiebe.trackacow.domain.repository.remote.CallRepositoryRemote

class CallRepositoryRemoteImpl(
    val firebaseDatabase: FirebaseDatabase,
    private val databasePath: String
): CallRepositoryRemote {

    override suspend fun insertCallRemote(callModel: CallModel) {
        val databaseReference: DatabaseReference = firebaseDatabase.getReference(databasePath)
        val cloudDatabaseId = databaseReference.push().key
        callModel.callCloudDatabaseId = cloudDatabaseId ?: ""
        databaseReference.child(cloudDatabaseId?:"").setValue(callModel)
    }

    override suspend fun insertCallListRemote(callModelList: List<CallModel>) {
        firebaseDatabase.getReference(databasePath).setValue(callModelList)
    }

    override fun updateCallRemote(callModel: CallModel) {
        firebaseDatabase.getReference("$databasePath/${callModel.callCloudDatabaseId}").setValue(callModel)
    }
}