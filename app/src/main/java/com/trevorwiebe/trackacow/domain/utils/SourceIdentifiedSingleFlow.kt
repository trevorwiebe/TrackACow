package com.trevorwiebe.trackacow.domain.utils

import kotlinx.coroutines.flow.Flow

data class SourceIdentifiedSingleFlow(
    var dataFlow: Flow<Pair<Any?, DataSource>>,
    val isFetchingFromCloud: Boolean
)
