package com.trevorwiebe.trackacow.domain.use_cases.drugs_given_use_cases

import android.app.Application
import com.trevorwiebe.trackacow.domain.repository.local.DrugsGivenRepository
import com.trevorwiebe.trackacow.domain.repository.remote.DrugsGivenRepositoryRemote
import com.trevorwiebe.trackacow.domain.utils.Utility

data class DeleteDrugsGivenByCowId(
    private val drugsGivenRepository: DrugsGivenRepository,
    private val drugsGivenRemoteRepository: DrugsGivenRepositoryRemote,
    private val context: Application
) {

    suspend operator fun invoke(cowId: String) {

        drugsGivenRepository.deleteDrugsGivenByCowId(cowId)

        if (Utility.haveNetworkConnection(context)) {
            drugsGivenRemoteRepository.deleteRemoteDrugsGivenByCowId(cowId)
        } else {
            Utility.setNewDataToUpload(context, true)
            // TODO: Add code to delete DrugsGiven when offline
        }
    }

}
