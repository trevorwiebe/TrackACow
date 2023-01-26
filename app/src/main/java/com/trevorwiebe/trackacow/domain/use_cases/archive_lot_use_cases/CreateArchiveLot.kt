package com.trevorwiebe.trackacow.domain.use_cases.archive_lot_use_cases

import android.app.Application
import com.trevorwiebe.trackacow.data.mapper.toArchiveLotModel
import com.trevorwiebe.trackacow.data.mapper.toCacheArchiveLotModel
import com.trevorwiebe.trackacow.domain.models.lot.LotModel
import com.trevorwiebe.trackacow.domain.repository.local.ArchiveLotRepository
import com.trevorwiebe.trackacow.domain.repository.remote.ArchiveLotRepositoryRemote
import com.trevorwiebe.trackacow.domain.use_cases.GetCloudDatabaseId
import com.trevorwiebe.trackacow.domain.utils.Constants
import com.trevorwiebe.trackacow.domain.utils.Utility

data class CreateArchiveLot(
    private val archiveLotRepository: ArchiveLotRepository,
    private val archiveLotRepositoryRemote: ArchiveLotRepositoryRemote,
    private val getCloudDatabaseId: GetCloudDatabaseId,
    private val context: Application
) {

    suspend operator fun invoke(lotModel: LotModel) {

        val archiveLotModel = lotModel.toArchiveLotModel(System.currentTimeMillis())

        archiveLotModel.lotId = getCloudDatabaseId.invoke("")

        val primaryKey = archiveLotRepository.insertArchiveLot(archiveLotModel)

        archiveLotModel.primaryKey = primaryKey.toInt()

        if (Utility.haveNetworkConnection(context)) {
            archiveLotRepositoryRemote.insertArchiveLotRemote(archiveLotModel)
        } else {
            archiveLotRepository.insertCacheArchiveLot(
                archiveLotModel.toCacheArchiveLotModel(Constants.INSERT_UPDATE)
            )
        }
    }
}