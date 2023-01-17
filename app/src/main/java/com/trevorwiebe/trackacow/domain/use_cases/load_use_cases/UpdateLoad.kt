package com.trevorwiebe.trackacow.domain.use_cases.load_use_cases

import android.app.Application
import com.trevorwiebe.trackacow.data.mapper.toCacheLoadModel
import com.trevorwiebe.trackacow.domain.models.load.LoadModel
import com.trevorwiebe.trackacow.domain.repository.local.LoadRepository
import com.trevorwiebe.trackacow.domain.repository.remote.LoadRemoteRepository
import com.trevorwiebe.trackacow.domain.utils.Constants
import com.trevorwiebe.trackacow.domain.utils.Utility

data class UpdateLoad(
    private val loadRepository: LoadRepository,
    private val loadRemoteRepository: LoadRemoteRepository,
    private val context: Application
){
    suspend operator fun invoke(loadModel: LoadModel) {

        val isConnected = Utility.haveNetworkConnection(context)

        loadRepository.updateLoad(loadModel)

        if (isConnected) {
            loadRemoteRepository.insertOrUpdateRemoteLoad(loadModel)
        } else {
            loadRepository.insertCacheLoad(loadModel.toCacheLoadModel(Constants.INSERT_UPDATE))
            Utility.setNewDataToUpload(context, true)
        }
    }
}
