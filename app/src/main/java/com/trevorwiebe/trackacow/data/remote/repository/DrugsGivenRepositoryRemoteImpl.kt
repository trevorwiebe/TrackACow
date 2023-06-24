package com.trevorwiebe.trackacow.data.remote.repository

import com.google.firebase.database.*
import com.trevorwiebe.trackacow.data.mapper.toDrugGivenModel
import com.trevorwiebe.trackacow.domain.models.drug.DrugModel
import com.trevorwiebe.trackacow.domain.models.drug_given.CacheDrugGivenModel
import com.trevorwiebe.trackacow.domain.models.drug_given.DrugGivenModel
import com.trevorwiebe.trackacow.domain.repository.remote.DrugsGivenRepositoryRemote
import com.trevorwiebe.trackacow.domain.utils.Constants
import com.trevorwiebe.trackacow.domain.utils.combineQueryDatabaseNodes
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import javax.inject.Provider

class DrugsGivenRepositoryRemoteImpl(
    val firebaseDatabase: FirebaseDatabase,
    val firebaseUserId: Provider<String>
) : DrugsGivenRepositoryRemote {
    override suspend fun createDrugsGivenListRemote(drugsGivenList: List<DrugGivenModel>) {
        val userId = firebaseUserId.get().toString()
        if (userId.isNotEmpty()) {
            drugsGivenList.forEach {
                if (!it.drugsGivenId.isNullOrEmpty()) {
                    firebaseDatabase.getReference(
                        "/users/${userId}/drugsGiven/${it.drugsGivenId}"
                    ).setValue(it)
                }
            }
        }
    }

    override fun readDrugsGivenAndDrugsByLotIdRemote(lotId: String):
            Flow<Pair<List<DrugModel>, List<DrugGivenModel>>> {
        val userId = firebaseUserId.get().toString()
        return if (userId.isNotEmpty()) {
            val drugRef = firebaseDatabase.getReference("/users/${userId}/drugs")
            val drugsGivenQuery = firebaseDatabase
                .getReference("/users/${userId}/drugsGiven")
                .orderByChild("drugsGivenLotId")
                .equalTo(lotId)
            combineQueryDatabaseNodes(
                drugRef, drugsGivenQuery,
                DrugModel::class.java, DrugGivenModel::class.java
            )
        } else {
            emptyFlow()
        }
    }

    override fun readDrugsGivenAndDrugsByCowIdRemote(cowId: String):
            Flow<Pair<List<DrugModel>, List<DrugGivenModel>>> {
        val userId = firebaseUserId.get().toString()
        return if (userId.isNotEmpty()) {
            val drugRef = firebaseDatabase.getReference("/users/${userId}/drugs")
            val drugsGivenQuery = firebaseDatabase
                .getReference("/users/${userId}/drugsGiven")
                .orderByChild("drugsGivenCowId")
                .equalTo(cowId)
            combineQueryDatabaseNodes(
                drugRef, drugsGivenQuery,
                DrugModel::class.java, DrugGivenModel::class.java
            )
        } else {
            emptyFlow()
        }
    }

    override suspend fun insertCacheDrugsGiven(drugsGivenList: List<CacheDrugGivenModel>) {
        val userId = firebaseUserId.get().toString()
        if (drugsGivenList.isNotEmpty() && userId.isNotEmpty()) {
            val databasePath = "/users/${userId}/drugsGiven"
            drugsGivenList.forEach { cacheDrugGivenModel ->
                val cacheDrugRef = firebaseDatabase.getReference(databasePath)
                if (cacheDrugGivenModel.drugGivenId != null) {
                    if (cacheDrugGivenModel.whatHappened == Constants.DELETE) {
                        cacheDrugRef.child(cacheDrugGivenModel.drugGivenId!!).removeValue()
                    } else {
                        val drugModel = cacheDrugGivenModel.toDrugGivenModel()
                        cacheDrugRef.child(cacheDrugGivenModel.drugGivenId!!).setValue(drugModel)
                    }
                }
            }
        }
    }

    override suspend fun editRemoteDrugsGiven(drugsGivenModel: DrugGivenModel) {
        val userId = firebaseUserId.get().toString()
        if (!drugsGivenModel.drugsGivenId.isNullOrEmpty() && userId.isNotEmpty()) {
            firebaseDatabase.getReference(
                "/users/${userId}/drugsGiven/${drugsGivenModel.drugsGivenId}"
            ).setValue(drugsGivenModel)
        }
    }

    override suspend fun updateDrugsGivenWithNewLotIdRemote(
        lotIdToSave: String,
        lotIdListToDelete: List<String>
    ) {
        val userId = firebaseUserId.get().toString()
        if (userId.isNotEmpty()) {
            val databasePath = "/users/${userId}/drugsGiven/"
            lotIdListToDelete.forEach { lotIdStr ->
                val drugsGivenQuery =
                    firebaseDatabase.getReference(databasePath).orderByChild("drugsGivenLotId")
                        .equalTo(lotIdStr)
                drugsGivenQuery.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        snapshot.children.forEach {
                            val drugGivenModel = it.getValue(DrugGivenModel::class.java)
                            val drugsGivenId = drugGivenModel?.drugsGivenId
                            if (drugsGivenId != null) {
                                firebaseDatabase.getReference(databasePath).child(drugsGivenId)
                                    .child("drugsGivenLotId").setValue(lotIdToSave)
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {}
                })
            }
        }
    }

    override suspend fun deleteRemoteDrugsGivenByCowId(cowId: String) {
        val userId = firebaseUserId.get().toString()
        if (userId.isNotEmpty()) {
            val databasePath = "/users/${userId}/drugsGiven/"
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
    }

    override suspend fun deleteRemoteDrugGivenByDrugGivenId(drugGivenId: String) {
        val userId = firebaseUserId.get().toString()
        if (userId.isNotEmpty()) {
            firebaseDatabase.getReference("/users/${userId}/drugsGiven/$drugGivenId").removeValue()
        }
    }

}
