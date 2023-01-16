package com.trevorwiebe.trackacow.domain.use_cases.load_use_cases

import android.content.Context
import com.trevorwiebe.trackacow.data.mapper.toCacheLoadModel
import com.trevorwiebe.trackacow.domain.models.load.LoadModel
import com.trevorwiebe.trackacow.domain.repository.local.LoadRepository
import com.trevorwiebe.trackacow.domain.repository.remote.LoadRemoteRepository
import com.trevorwiebe.trackacow.domain.use_cases.GetCloudDatabaseId
import com.trevorwiebe.trackacow.domain.utils.Constants
import com.trevorwiebe.trackacow.domain.utils.Utility

data class DeleteLoad(
    private val loadRepository: LoadRepository,
    private val loadRemoteRepository: LoadRemoteRepository,
    private val getCloudDatabaseId: GetCloudDatabaseId,
    private val context: Context
) {
    suspend operator fun invoke(loadModel: LoadModel) {

        loadRepository.deleteLoad(loadModel)

        if (Utility.haveNetworkConnection(context)) {
            loadRemoteRepository.deleteRemoteLoad(loadModel)
        } else {
            Utility.setNewDataToUpload(context, true)
            loadRepository.insertCacheLoad(loadModel.toCacheLoadModel(Constants.DELETE))
        }
    }
}
