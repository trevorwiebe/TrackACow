package com.trevorwiebe.trackacow.domain.use_cases

import assertk.assertThat
import assertk.assertions.isEqualTo
import org.junit.jupiter.api.Test

class CalculateDayStartAndDayEndTest {

    @Test
    fun testZeroInputValue_returnsZeroAndZero() {

        val listOfDates = CalculateDayStartAndDayEnd().invoke(0L)

        assertThat(listOfDates.size).isEqualTo(2)
        assertThat(listOfDates[0]).isEqualTo(0)
        assertThat(listOfDates[1]).isEqualTo(0)
    }

    @Test
    fun testNegativeInputValue_returnsZeroAndZero() {

        val listOfDates = CalculateDayStartAndDayEnd().invoke(-45L)

        assertThat(listOfDates.size).isEqualTo(2)
        assertThat(listOfDates[0]).isEqualTo(0)
        assertThat(listOfDates[1]).isEqualTo(0)
    }

    @Test
    fun testValuesCalculated_returnsCorrectValue() {

        val septSat16Mid = 1694914745150
        val septSat16Start = 1694840400000
        val septSat16End = 1694926799999

        val listOfDates = CalculateDayStartAndDayEnd().invoke(septSat16Mid)

        assertThat(listOfDates.size).isEqualTo(2)
        assertThat(listOfDates[0]).isEqualTo(septSat16Start)
        assertThat(listOfDates[1]).isEqualTo(septSat16End)
    }

}