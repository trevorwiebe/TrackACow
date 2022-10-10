package com.trevorwiebe.trackacow.data.remote.repository

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.trevorwiebe.trackacow.domain.models.pen.PenModel
import com.trevorwiebe.trackacow.domain.repository.remote.PenRepositoryRemote

class PenRepositoryRemoteImpl(
    val firebaseDatabase: FirebaseDatabase,
    private val databasePath: String
): PenRepositoryRemote {

    override suspend fun insertPenRemote(penModel: PenModel) {
        val databaseReference: DatabaseReference = firebaseDatabase.getReference(databasePath)
        val cloudDatabaseId = databaseReference.push().key
        penModel.penCloudDatabaseId = cloudDatabaseId
        databaseReference.child(cloudDatabaseId?:"").setValue(penModel)
    }

}