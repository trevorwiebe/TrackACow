package com.trevorwiebe.trackacow.domain.use_cases.pen_use_cases

import android.app.Application
import com.trevorwiebe.trackacow.data.mapper.toCachePenModel
import com.trevorwiebe.trackacow.domain.models.pen.PenModel
import com.trevorwiebe.trackacow.domain.repository.local.PenRepository
import com.trevorwiebe.trackacow.domain.repository.remote.PenRepositoryRemote
import com.trevorwiebe.trackacow.domain.utils.Constants
import com.trevorwiebe.trackacow.domain.utils.Utility

class CreatePenUC(
    private val penRepository: PenRepository,
    private val penRemoteRepository: PenRepositoryRemote,
    private val context: Application
) {
    suspend operator fun invoke(penModel: PenModel){

        val id: Long = penRepository.insertPen(penModel)

        penModel.penPrimaryKey = id.toInt()

        if(Utility.haveNetworkConnection(context)){
            penRemoteRepository.insertPenRemote(penModel)
        }else{
            penRepository.insertCachePen(penModel.toCachePenModel(Constants.INSERT_UPDATE))
            Utility.setNewDataToUpload(context, true)
        }

    }
}