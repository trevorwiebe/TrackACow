package com.trevorwiebe.trackacow.data.remote.repository

import com.google.firebase.database.FirebaseDatabase
import com.trevorwiebe.trackacow.domain.models.lot.CacheLotModel
import com.trevorwiebe.trackacow.domain.models.lot.LotModel
import com.trevorwiebe.trackacow.domain.repository.remote.LotRepositoryRemote

class LotRepositoryRemoteImpl(
    private val firebaseDatabase: FirebaseDatabase,
    private val databasePath: String
): LotRepositoryRemote {

    override fun archiveLot(lotModel: LotModel) {
        firebaseDatabase.getReference("$databasePath/${lotModel.lotCloudDatabaseId}/archived")
            .setValue(1)
        firebaseDatabase.getReference("$databasePath/${lotModel.lotCloudDatabaseId}/dateArchived")
            .setValue(System.currentTimeMillis())
    }

    override fun updateLotWithNewPenIdRemote(lotId: String, penId: String) {
        firebaseDatabase.getReference("$databasePath/$lotId/lotPenCloudDatabaseId").setValue(penId)
    }

    override fun insertCacheLotRemote(lotList: List<CacheLotModel>) {

    }

    override fun insertAndUpdateLotRemote(lotModel: LotModel) {
        if (lotModel.lotCloudDatabaseId.isNotEmpty()) {
            firebaseDatabase.getReference(
                "$databasePath/${lotModel.lotCloudDatabaseId}"
            ).setValue(lotModel)
        }
    }

    override fun deleteLotRemote(lotModel: LotModel) {
        if (lotModel.lotCloudDatabaseId.isNotEmpty()) {
            firebaseDatabase.getReference(
                "$databasePath/${lotModel.lotCloudDatabaseId}"
            ).removeValue()
        }
    }

    override fun deleteLotsByIdRemote(lotIdList: List<String>) {
        lotIdList.forEachIndexed { index, _ ->
            firebaseDatabase.getReference(databasePath).child(lotIdList[index]).removeValue()
        }
    }

}