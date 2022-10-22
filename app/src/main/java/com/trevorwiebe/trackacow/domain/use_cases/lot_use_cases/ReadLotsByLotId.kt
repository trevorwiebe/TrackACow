package com.trevorwiebe.trackacow.domain.use_cases.lot_use_cases

import com.trevorwiebe.trackacow.domain.models.lot.LotModel
import com.trevorwiebe.trackacow.domain.repository.local.LotRepository
import kotlinx.coroutines.flow.Flow

data class ReadLotsByLotId(
    private val lotRepository: LotRepository
) {
    operator fun invoke(lotId: Int): Flow<LotModel?>{
        return lotRepository.readLotByLotId(lotId)
    }
}