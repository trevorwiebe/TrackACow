package com.trevorwiebe.trackacow.domain.repository.remote

import com.trevorwiebe.trackacow.domain.models.drug.DrugModel

interface DrugRepositoryRemote {
    fun insertDrug(drugModel: DrugModel)
}