package com.trevorwiebe.trackacow.data.remote.repository

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
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

    override suspend fun updateCowWithNewLotIdRemote(
        lotIdToSave: String,
        lotIdToDeleteList: List<String>
    ) {
        lotIdToDeleteList.forEach { lotIdStr ->
            val cowQuery =
                firebaseDatabase.getReference(databasePath).orderByChild("lotId").equalTo(lotIdStr)
            cowQuery.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.children.forEach {
                        val cowModel = it.getValue(CowModel::class.java)
                        val cowId = cowModel?.cowId
                        if (cowId != null) {
                            firebaseDatabase.getReference(databasePath).child(cowId).child("lotId")
                                .setValue(lotIdToSave)
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })
        }
    }


}