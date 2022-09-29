package com.trevorwiebe.trackacow.domain.models.pen

data class CachePenModel(
    var primaryKey: Int,
    var penId: String,
    var penName: String,
    var whatHappened: Int
)
