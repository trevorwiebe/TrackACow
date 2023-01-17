package com.trevorwiebe.trackacow.domain.use_cases.pen_use_cases

import android.app.Application
import com.trevorwiebe.trackacow.data.mapper.toCachePenModel
import com.trevorwiebe.trackacow.domain.models.pen.PenModel
import com.trevorwiebe.trackacow.domain.repository.local.PenRepository
import com.trevorwiebe.trackacow.domain.repository.remote.PenRepositoryRemote
import com.trevorwiebe.trackacow.domain.utils.Constants
import com.trevorwiebe.trackacow.domain.utils.Utility

class UpdatePenUC(
    private val penRepository: PenRepository,
    private val penRepositoryRemote: PenRepositoryRemote,
    private val context: Application
) {
    suspend operator fun invoke(penModel: PenModel){
        
        penRepository.updatePen(penModel)

        if(Utility.haveNetworkConnection(context)){
            // insertPenRemote is the same code as updatePenRemote would be
            penRepositoryRemote.insertAndUpdatePenRemote(penModel)
        }else{
            Utility.setNewDataToUpload(context, true)
            penRepository.insertCachePen(penModel.toCachePenModel(Constants.INSERT_UPDATE))
        }

    }
}