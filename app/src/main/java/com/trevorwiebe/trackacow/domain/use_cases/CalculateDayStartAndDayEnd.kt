package com.trevorwiebe.trackacow.domain.use_cases

import java.util.*

class CalculateDayStartAndDayEnd {

    operator fun invoke(date: Long): List<Long> {

        if (date <= 0L) {
            return listOf(0, 0)
        }

        val timeList: MutableList<Long> = mutableListOf()

        val startCalendar = Calendar.getInstance()
        startCalendar.timeInMillis = date
        startCalendar.set(Calendar.MILLISECOND, 0)
        startCalendar.set(Calendar.SECOND, 0)
        startCalendar.set(Calendar.MINUTE, 0)
        startCalendar.set(Calendar.HOUR_OF_DAY, 0)
        timeList.add(startCalendar.timeInMillis)

        val endCalendar = Calendar.getInstance()
        endCalendar.timeInMillis = date
        endCalendar.set(Calendar.MILLISECOND, 999)
        endCalendar.set(Calendar.SECOND, 59)
        endCalendar.set(Calendar.MINUTE, 59)
        endCalendar.set(Calendar.HOUR_OF_DAY, 23)
        timeList.add(endCalendar.timeInMillis)

        return timeList.toList()
    }
}