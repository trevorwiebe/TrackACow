package com.trevorwiebe.trackacow.domain.utils

import kotlinx.coroutines.flow.Flow

data class SourceIdentifiedListFlow(
    var dataFlow: Flow<Pair<List<Any>, DataSource>>,
    var isFetchingFromCloud: Boolean,
)