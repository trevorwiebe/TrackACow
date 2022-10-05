package com.trevorwiebe.trackacow.domain.models.compound_model

data class PenAndLotModel(
    var penId: String = "",
    var penName: String = "",
    var lotName: String? = "",
    var lotId: String? = "",
    var customerName: String? = null,
    var notes: String? = null,
    var date: Long? = 0,
)
