package com.trevorwiebe.trackacow.domain.utils

sealed class DataSource {
    object Local : DataSource()
    object Cloud : DataSource()
}
