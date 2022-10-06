package com.trevorwiebe.trackacow.domain.repository.remote

interface LotRepositoryRemote {

    fun updateLotWithNewPenIdRemote(lotId: String, penId: String)

}