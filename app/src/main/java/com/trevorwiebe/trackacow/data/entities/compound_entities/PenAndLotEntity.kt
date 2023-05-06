package com.trevorwiebe.trackacow.data.entities.compound_entities

data class PenAndLotEntity(
    var penPrimaryKey: Int = 0,
    var penCloudDatabaseId: String? = "",
    var penName: String = "",
    var lotPrimaryKey: Int? = 0,
    var lotName: String? = "",
    var lotCloudDatabaseId: String? = "",
    var customerName: String? = null,
    var notes: String? = null,
    var date: Long? = 0,
    var archived: Long = 0,
    var dateArchived: Long = 0
)
