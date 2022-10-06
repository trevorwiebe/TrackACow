package com.trevorwiebe.trackacow.data.remote.repository

import com.google.firebase.database.FirebaseDatabase
import com.trevorwiebe.trackacow.domain.repository.remote.LotRepositoryRemote

class LotRepositoryRemoteImpl(
    val firebaseDatabase: FirebaseDatabase,
    private val databasePath: String
): LotRepositoryRemote {

    override fun updateLotWithNewPenIdRemote(lotId: String, penId: String) {
        firebaseDatabase.getReference("$databasePath/$lotId/lotPenId").setValue(penId)
    }

}