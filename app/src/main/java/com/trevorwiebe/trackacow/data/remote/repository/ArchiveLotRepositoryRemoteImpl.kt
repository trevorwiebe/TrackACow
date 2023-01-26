package com.trevorwiebe.trackacow.data.remote.repository

import com.google.firebase.database.FirebaseDatabase
import com.trevorwiebe.trackacow.domain.models.archive_lot.ArchiveLotModel
import com.trevorwiebe.trackacow.domain.repository.remote.ArchiveLotRepositoryRemote

class ArchiveLotRepositoryRemoteImpl(
    val firebaseDatabase: FirebaseDatabase,
    private val databasePath: String
) : ArchiveLotRepositoryRemote {

    override fun insertArchiveLotRemote(archiveLotModel: ArchiveLotModel) {
        if (!archiveLotModel.lotId.isNullOrEmpty()) {
            firebaseDatabase.getReference(
                "$databasePath/${archiveLotModel.lotId}"
            ).setValue(archiveLotModel)
        }
    }

}