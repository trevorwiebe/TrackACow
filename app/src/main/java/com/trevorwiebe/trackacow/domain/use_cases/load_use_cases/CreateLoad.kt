package com.trevorwiebe.trackacow.domain.use_cases.load_use_cases

import android.app.Application
import com.trevorwiebe.trackacow.data.mapper.toCacheLoadModel
import com.trevorwiebe.trackacow.domain.models.load.LoadModel
import com.trevorwiebe.trackacow.domain.repository.local.LoadRepository
import com.trevorwiebe.trackacow.domain.repository.remote.LoadRemoteRepository
import com.trevorwiebe.trackacow.domain.use_cases.GetCloudDatabaseId
import com.trevorwiebe.trackacow.domain.utils.Constants
import com.trevorwiebe.trackacow.domain.utils.Utility

class CreateLoad(
    private val loadRepository: LoadRepository,
    private val loadRemoteRepository: LoadRemoteRepository,
    private val getCloudDatabaseId: GetCloudDatabaseId,
    private val context: Application
) {

    suspend operator fun invoke(loadModel: LoadModel) {

        loadModel.loadId = getCloudDatabaseId.invoke("")

        loadRepository.insertLoad(loadModel)

        if (Utility.haveNetworkConnection(context)) {
            loadRemoteRepository.insertOrUpdateRemoteLoad(loadModel)
        } else {
            Utility.setNewDataToUpload(context, true)
            loadRepository.insertCacheLoad(loadModel.toCacheLoadModel(Constants.INSERT_UPDATE))
        }
    }
}