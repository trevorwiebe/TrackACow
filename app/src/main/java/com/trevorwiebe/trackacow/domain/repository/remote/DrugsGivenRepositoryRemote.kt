package com.trevorwiebe.trackacow.domain.repository.remote

interface DrugsGivenRepositoryRemote {

    suspend fun deleteRemoteDrugsGivenByCowId(cowId: String)
}