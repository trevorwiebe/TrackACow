package com.trevorwiebe.trackacow.domain.use_cases

import java.util.*

class CalculateDayStartAndDayEnd {

    operator fun invoke(date: Long): List<Long> {

        val timeList: MutableList<Long> = mutableListOf()

        val startCalendar = Calendar.getInstance()
        startCalendar.timeInMillis = date
        startCalendar.set(Calendar.MILLISECOND, 0)
        startCalendar.set(Calendar.SECOND, 0)
        startCalendar.set(Calendar.MINUTE, 0)
        startCalendar.set(Calendar.HOUR, 0)
        timeList.add(startCalendar.timeInMillis)

        val endCalendar = Calendar.getInstance()
        startCalendar.timeInMillis = date
        startCalendar.set(Calendar.MILLISECOND, 999)
        startCalendar.set(Calendar.SECOND, 59)
        startCalendar.set(Calendar.MINUTE, 59)
        startCalendar.set(Calendar.HOUR, 23)
        timeList.add(endCalendar.timeInMillis)

        return timeList.toList()
    }
}