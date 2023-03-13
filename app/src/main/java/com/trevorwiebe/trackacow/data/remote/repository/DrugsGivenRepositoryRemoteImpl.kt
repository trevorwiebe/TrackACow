package com.trevorwiebe.trackacow.data.remote.repository

import com.google.firebase.database.*
import com.trevorwiebe.trackacow.domain.models.drug_given.DrugGivenModel
import com.trevorwiebe.trackacow.domain.repository.remote.DrugsGivenRepositoryRemote

class DrugsGivenRepositoryRemoteImpl(
    val firebaseDatabase: FirebaseDatabase,
    private val databasePath: String
) : DrugsGivenRepositoryRemote {
    override suspend fun createDrugsGivenListRemote(drugsGivenList: List<DrugGivenModel>) {
        drugsGivenList.forEach {
            if (!it.drugsGivenId.isNullOrEmpty()) {
                firebaseDatabase.getReference(
                    "$databasePath/${it.drugsGivenId}"
                ).setValue(it)
            }
        }
    }

    override suspend fun editRemoteDrugsGiven(drugsGivenModel: DrugGivenModel) {
        if (!drugsGivenModel.drugsGivenId.isNullOrEmpty()) {
            firebaseDatabase.getReference(
                "$databasePath/${drugsGivenModel.drugsGivenId}"
            ).setValue(drugsGivenModel)
        }
    }

    override suspend fun deleteRemoteDrugsGivenByCowId(cowId: String) {
        val fbQuery: Query = firebaseDatabase.getReference(databasePath)
            .orderByChild("drugsGivenCowId")
            .equalTo(cowId)

        fbQuery.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach {
                    it.ref.removeValue()
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    override suspend fun deleteRemoteDrugGivenByDrugGivenId(drugGivenId: String) {
        firebaseDatabase.getReference("$databasePath/$drugGivenId").removeValue()
    }

}
