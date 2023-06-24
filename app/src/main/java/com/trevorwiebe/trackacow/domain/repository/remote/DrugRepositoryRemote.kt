package com.trevorwiebe.trackacow.domain.repository.remote

import com.trevorwiebe.trackacow.domain.models.drug.CacheDrugModel
import com.trevorwiebe.trackacow.domain.models.drug.DrugModel
import kotlinx.coroutines.flow.Flow

interface DrugRepositoryRemote {
    fun insertDrug(drugModel: DrugModel)

    fun readDrugList(): Flow<List<DrugModel>>

    fun deleteDrug(drugModel: DrugModel)

    fun insertCacheDrugs(cacheDrugsList: List<CacheDrugModel>)
}