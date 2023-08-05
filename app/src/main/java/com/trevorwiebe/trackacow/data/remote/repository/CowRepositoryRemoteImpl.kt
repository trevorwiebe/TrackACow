package com.trevorwiebe.trackacow.data.remote.repository

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.trevorwiebe.trackacow.data.mapper.toCowModel
import com.trevorwiebe.trackacow.domain.models.cow.CacheCowModel
import com.trevorwiebe.trackacow.domain.models.cow.CowModel
import com.trevorwiebe.trackacow.domain.repository.remote.CowRepositoryRemote
import com.trevorwiebe.trackacow.domain.utils.Constants
import com.trevorwiebe.trackacow.domain.utils.addQueryListValueEventListenerFlow
import com.trevorwiebe.trackacow.domain.utils.addSingleValueEventListenerFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import javax.inject.Provider

class CowRepositoryRemoteImpl(
    val firebaseDatabase: FirebaseDatabase,
    private val firebaseUserId: Provider<String>
) : CowRepositoryRemote {


    override suspend fun insertCowRemote(cowModel: CowModel) {
        val userId = firebaseUserId.get().toString()
        if (cowModel.cowId.isNotEmpty() && userId.isNotEmpty()) {
            firebaseDatabase.getReference(
                "/users/${userId}/cows/${cowModel.cowId}"
            ).setValue(cowModel)
        }
    }

    override fun readCowByCowIdRemote(cowId: String): Flow<CowModel?> {
        val userId = firebaseUserId.get().toString()
        return if (userId.isNotEmpty()) {
            val firebaseRef = firebaseDatabase.getReference("/users/$userId/cows/$cowId")
            firebaseRef.addSingleValueEventListenerFlow(CowModel::class.java)
        } else {
            emptyFlow()
        }
    }

    override fun readCowsByLotId(lotId: String): Flow<List<CowModel>> {
        val userId = firebaseUserId.get().toString()
        return if (userId.isNotEmpty()) {
            val cowQuery = firebaseDatabase
                .getReference("/users/$userId/cows")
                .orderByChild("lotId")
                .equalTo(lotId)
            cowQuery.addQueryListValueEventListenerFlow(CowModel::class.java)
        } else {
            emptyFlow()
        }
    }

    override suspend fun deleteCowRemote(cowModel: CowModel) {
        val userId = firebaseUserId.get().toString()
        if (cowModel.cowId.isNotEmpty() && userId.isNotEmpty()) {
            firebaseDatabase.getReference(
                "/users/${userId}/cows/${cowModel.cowId}"
            ).removeValue()
        }
    }

    override suspend fun updateCowWithNewLotIdRemote(
        lotIdToSave: String,
        lotIdToDeleteList: List<String>
    ) {
        val userId = firebaseUserId.get().toString()
        if (userId.isNotEmpty()) {
            val databasePath = "/users/${userId}/cows"
            lotIdToDeleteList.forEach { lotIdStr ->
                val cowQuery = firebaseDatabase
                    .getReference(databasePath)
                    .orderByChild("lotId")
                    .equalTo(lotIdStr)
                cowQuery.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        snapshot.children.forEach {
                            val cowModel = it.getValue(CowModel::class.java)
                            val cowId = cowModel?.cowId
                            if (cowId != null) {
                                firebaseDatabase.getReference(databasePath).child(cowId)
                                    .child("lotId")
                                    .setValue(lotIdToSave)
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {}
                })
            }
        }
    }

    override suspend fun insertCacheCowsRemote(cacheCowModelList: List<CacheCowModel>) {
        val userId = firebaseUserId.get().toString()
        if (cacheCowModelList.isNotEmpty() && userId.isNotEmpty()) {
            val databasePath = "/users/${userId}/cows"
            cacheCowModelList.forEach { cacheCowModel ->
                val cacheCowRef = firebaseDatabase.getReference(databasePath)
                if (cacheCowModel.whatHappened == Constants.DELETE) {
                    cacheCowRef.child(cacheCowModel.cowId).removeValue()
                } else {
                    val cowModel = cacheCowModel.toCowModel()
                    cacheCowRef.child(cacheCowModel.cowId).setValue(cowModel)
                }
            }
        }
    }


}