package com.trevorwiebe.trackacow.data.remote.repository

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.trevorwiebe.trackacow.domain.models.drug.DrugModel
import com.trevorwiebe.trackacow.domain.repository.remote.DrugRepositoryRemote

class DrugRepositoryRemoteImpl(
    val firebaseDatabase: FirebaseDatabase,
    private val databasePath: String
): DrugRepositoryRemote {

    override fun insertDrug(drugModel: DrugModel) {
        val databaseReference: DatabaseReference = firebaseDatabase.getReference(databasePath)
        val cloudDatabaseId = databaseReference.push().key
        drugModel.drugId = cloudDatabaseId ?: ""
        databaseReference.child(cloudDatabaseId?:"").setValue(drugModel)
    }

}