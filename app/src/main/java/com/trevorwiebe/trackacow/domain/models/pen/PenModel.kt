package com.trevorwiebe.trackacow.domain.models.pen

data class PenModel(
    var penPrimaryKey: Int = 0,
    var penCloudDatabaseId: String? = "",
    var penName: String = "",
)
