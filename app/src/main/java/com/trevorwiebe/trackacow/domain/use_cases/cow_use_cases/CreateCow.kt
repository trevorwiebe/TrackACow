package com.trevorwiebe.trackacow.domain.use_cases.cow_use_cases

import android.app.Application
import com.trevorwiebe.trackacow.data.mapper.toCacheCowModel
import com.trevorwiebe.trackacow.domain.models.cow.CowModel
import com.trevorwiebe.trackacow.domain.repository.local.CowRepository
import com.trevorwiebe.trackacow.domain.repository.remote.CowRepositoryRemote
import com.trevorwiebe.trackacow.domain.use_cases.GetCloudDatabaseId
import com.trevorwiebe.trackacow.domain.utils.Constants
import com.trevorwiebe.trackacow.domain.utils.Utility

class CreateCow(
    private val cowRepository: CowRepository,
    private val cowRepositoryRemote: CowRepositoryRemote,
    private val getCloudDatabaseId: GetCloudDatabaseId,
    private val context: Application
) {

    suspend operator fun invoke(cowModel: CowModel): String {

        val cloudCowId = getCloudDatabaseId("")
        cowModel.cowId = cloudCowId

        cowRepository.insertCow(cowModel)

        if (Utility.haveNetworkConnection(context)) {
            cowRepositoryRemote.insertCowRemote(cowModel)
        } else {
            Utility.setNewDataToUpload(context, true)
            cowRepository.insertCacheCow(cowModel.toCacheCowModel(Constants.INSERT_UPDATE))
        }

        return cloudCowId
    }
}