package com.trevorwiebe.trackacow.data.entities.compound_entities

data class PenAndLotEntity(
    var penPenId: String = "",
    var penName: String = "",
    var lotName: String? = "",
    var lotId: String? = "",
    var customerName: String? = null,
    var notes: String? = null,
    var date: Long? = 0,
)
