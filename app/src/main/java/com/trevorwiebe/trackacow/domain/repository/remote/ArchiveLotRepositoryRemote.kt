package com.trevorwiebe.trackacow.domain.repository.remote

import com.trevorwiebe.trackacow.domain.models.archive_lot.ArchiveLotModel

interface ArchiveLotRepositoryRemote {

    fun insertArchiveLotRemote(archiveLotModel: ArchiveLotModel)
}