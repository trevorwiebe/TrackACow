package com.trevorwiebe.trackacow.domain.use_cases.cow_use_cases

import android.app.Application
import com.trevorwiebe.trackacow.domain.models.cow.CowModel
import com.trevorwiebe.trackacow.domain.repository.local.CowRepository
import com.trevorwiebe.trackacow.domain.repository.remote.CowRepositoryRemote
import com.trevorwiebe.trackacow.domain.utils.Utility
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

class ReadCowsByLotId(
        private val cowRepository: CowRepository,
        private val cowRepositoryRemote: CowRepositoryRemote,
        private val context: Application
) {

    // TODO: update this with data source identification

    @OptIn(FlowPreview::class)
    operator fun invoke(lotId: String): Flow<List<CowModel>> {
        val localFlow = cowRepository.getCowsByLotId(lotId)
        val cowCloudFlow = cowRepositoryRemote.readCowsByLotId(lotId)

        return if (Utility.haveNetworkConnection(context)) {
            localFlow
                .flatMapConcat { localData ->
                    cowCloudFlow.onStart {
                            emit(localData)
                        }.map { cowModelList ->
                            cowRepository.insertOrUpdateCowList(cowModelList)
                            cowModelList
                        }
                    }
        } else {
            localFlow
        }

    }
}
