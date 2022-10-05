package com.trevorwiebe.trackacow.domain.use_cases.pen_use_cases

import com.trevorwiebe.trackacow.domain.models.pen.PenModel
import com.trevorwiebe.trackacow.domain.repository.local.PenRepository
import kotlinx.coroutines.flow.Flow

data class ReadPens(
    val penRepository: PenRepository
){
    operator fun invoke(): Flow<List<PenModel>> {
        return penRepository.readPens()
    }
}
