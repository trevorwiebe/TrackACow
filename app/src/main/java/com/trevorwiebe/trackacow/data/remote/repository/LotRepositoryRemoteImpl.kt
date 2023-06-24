package com.trevorwiebe.trackacow.data.remote.repository

import com.google.firebase.database.FirebaseDatabase
import com.trevorwiebe.trackacow.data.mapper.toLotModel
import com.trevorwiebe.trackacow.domain.models.lot.CacheLotModel
import com.trevorwiebe.trackacow.domain.models.lot.LotModel
import com.trevorwiebe.trackacow.domain.models.pen.PenModel
import com.trevorwiebe.trackacow.domain.repository.remote.LotRepositoryRemote
import com.trevorwiebe.trackacow.domain.utils.Constants
import com.trevorwiebe.trackacow.domain.utils.addQueryListValueEventListenerFlow
import com.trevorwiebe.trackacow.domain.utils.addSingleValueEventListenerFlow
import com.trevorwiebe.trackacow.domain.utils.combineQueryDatabaseNodes
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import javax.inject.Provider

class LotRepositoryRemoteImpl(
    private val firebaseDatabase: FirebaseDatabase,
    private val firebaseUserId: Provider<String>
): LotRepositoryRemote {

    override fun archiveLot(lotModel: LotModel) {
        val userId = firebaseUserId.get().toString()
        if (userId.isNotEmpty()) {
            val databasePath = "/users/${userId}/cattleLot"
            firebaseDatabase.getReference("$databasePath/${lotModel.lotCloudDatabaseId}/archived")
                .setValue(1)
            firebaseDatabase.getReference("$databasePath/${lotModel.lotCloudDatabaseId}/dateArchived")
                .setValue(System.currentTimeMillis())
        }
    }

    override fun updateLotWithNewPenIdRemote(lotId: String, penId: String) {
        val userId = firebaseUserId.get().toString()
        if (userId.isNotEmpty()) {
            firebaseDatabase.getReference("/users/${userId}/cattleLot/$lotId/lotPenCloudDatabaseId")
                .setValue(penId)
        }
    }

    override fun insertCacheLotRemote(lotList: List<CacheLotModel>) {
        val userId = firebaseUserId.get().toString()
        if (userId.isNotEmpty() && lotList.isNotEmpty()) {
            val lotRef = firebaseDatabase.getReference("/users/${userId}/cattleLot")
            lotList.forEach { cacheLotModel ->
                if (cacheLotModel.whatHappened == Constants.DELETE) {
                    lotRef.child(cacheLotModel.lotCloudDatabaseId).removeValue()
                } else {
                    val lotModel = cacheLotModel.toLotModel()
                    lotRef.child(cacheLotModel.lotCloudDatabaseId).setValue(lotModel)
                }
            }
        }
    }

    override fun insertAndUpdateLotRemote(lotModel: LotModel) {
        val userId = firebaseUserId.get().toString()
        if (lotModel.lotCloudDatabaseId.isNotEmpty() && userId.isNotEmpty()) {
            firebaseDatabase.getReference(
                "/users/${userId}/cattleLot/${lotModel.lotCloudDatabaseId}"
            ).setValue(lotModel)
        }
    }

    override fun readPenAndLotsIncludeEmptyPens(): Flow<Pair<List<PenModel>, List<LotModel>>> {
        val userId = firebaseUserId.get().toString()
        return if (userId.isNotEmpty()) {
            val penRef = firebaseDatabase.getReference("/users/${userId}/pens")
            val lotQuery = firebaseDatabase
                .getReference("/users/${userId}/cattleLot")
                .orderByChild("archived")
                .equalTo(0.toDouble())
            combineQueryDatabaseNodes(
                penRef, lotQuery,
                PenModel::class.java, LotModel::class.java
            )
        } else {
            emptyFlow()
        }
    }

    override fun readArchivedLots(): Flow<List<LotModel>> {
        val userId = firebaseUserId.get().toString()
        return if (userId.isNotEmpty()) {
            val archiveLotRef = firebaseDatabase
                .getReference("/users/${userId}/cattleLot")
                .orderByChild("archived")
                .equalTo(1.toDouble())
            archiveLotRef.addQueryListValueEventListenerFlow(LotModel::class.java)
        } else {
            emptyFlow()
        }
    }

    override fun readLots(): Flow<List<LotModel>> {
        val userId = firebaseUserId.get().toString()
        return if (userId.isNotEmpty()) {
            val lotRef = firebaseDatabase
                .getReference("/users/${userId}/cattleLot")
                .orderByChild("archived")
                .equalTo(0.toDouble())

            lotRef.addQueryListValueEventListenerFlow(LotModel::class.java)
        } else {
            emptyFlow()
        }
    }

    override fun readLotByLotId(lotId: String): Flow<LotModel> {
        val userId = firebaseUserId.get().toString()
        return if (userId.isNotEmpty()) {
            val lotRef = firebaseDatabase.getReference(
                "/users/${userId}/cattleLot/$lotId"
            )
            lotRef.addSingleValueEventListenerFlow(LotModel::class.java)
        } else {
            emptyFlow()
        }
    }

    override fun deleteLotRemote(lotModel: LotModel) {
        val userId = firebaseUserId.get().toString()
        if (lotModel.lotCloudDatabaseId.isNotEmpty() && userId.isNotEmpty()) {
            firebaseDatabase.getReference(
                "/users/${userId}/cattleLot/${lotModel.lotCloudDatabaseId}"
            ).removeValue()
        }
    }

    override fun deleteLotsByIdRemote(lotIdList: List<String>) {
        val userId = firebaseUserId.get().toString()
        if (userId.isNotEmpty()) {
            val databasePath = "users/${userId}/cattleLot"
            lotIdList.forEachIndexed { index, _ ->
                firebaseDatabase.getReference(databasePath).child(lotIdList[index]).removeValue()
            }
        }
    }

}