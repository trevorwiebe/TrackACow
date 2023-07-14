package com.trevorwiebe.trackacow.domain.use_cases.drugs_given_use_cases

import android.app.Application
import com.trevorwiebe.trackacow.data.mapper.toCacheDrugGivenModel
import com.trevorwiebe.trackacow.domain.repository.local.DrugsGivenRepository
import com.trevorwiebe.trackacow.domain.repository.remote.DrugsGivenRepositoryRemote
import com.trevorwiebe.trackacow.domain.utils.Constants
import com.trevorwiebe.trackacow.domain.utils.Utility

data class DeleteDrugsGivenByCowId(
    private val drugsGivenRepository: DrugsGivenRepository,
    private val drugsGivenRemoteRepository: DrugsGivenRepositoryRemote,
    private val context: Application
) {

    suspend operator fun invoke(cowId: String) {

        val deletedDrugsGiven = drugsGivenRepository.deleteDrugsGivenByCowIdTransaction(cowId)

        if (Utility.haveNetworkConnection(context)) {
            drugsGivenRemoteRepository.deleteRemoteDrugsGivenByCowId(cowId)
        } else {
            Utility.setNewDataToUpload(context, true)
            drugsGivenRepository.createCacheDrugsGivenList(
                    deletedDrugsGiven.map {
                        it.toCacheDrugGivenModel(Constants.DELETE)
                    }
            )
        }
    }

}
