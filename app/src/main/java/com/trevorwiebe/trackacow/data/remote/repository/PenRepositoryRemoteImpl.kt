package com.trevorwiebe.trackacow.data.remote.repository

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.trevorwiebe.trackacow.domain.models.pen.PenModel
import com.trevorwiebe.trackacow.domain.repository.remote.PenRepositoryRemote

class PenRepositoryRemoteImpl(
    val firebaseDatabase: FirebaseDatabase,
    private val databasePath: String
): PenRepositoryRemote {

    // Use this for insert and update
    override suspend fun insertPenRemote(penModel: PenModel) {
        val databaseReference: DatabaseReference = firebaseDatabase.getReference(databasePath)
        databaseReference.child(penModel.penCloudDatabaseId?:"").setValue(penModel)
    }

    override suspend fun deletePenRemote(penModel: PenModel) {
        firebaseDatabase.getReference("$databasePath/${penModel.penCloudDatabaseId}").removeValue()
    }
}