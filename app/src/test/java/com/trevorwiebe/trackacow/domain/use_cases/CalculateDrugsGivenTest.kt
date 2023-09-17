package com.trevorwiebe.trackacow.domain.use_cases

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.trevorwiebe.trackacow.domain.models.compound_model.drugsGivenAndDrugsModel
import org.junit.jupiter.api.Test

class CalculateDrugsGivenTest {

    @Test
    fun calculateDrugsGiven_negativeIsHandledAsZero() {

        val drugsGivenAndDrugModel1 = drugsGivenAndDrugsModel().copy(
            drugsGivenDrugId = "id1", drugsGivenAmountGiven = 20
        )
        val drugsGivenAndDrugModel2 = drugsGivenAndDrugsModel().copy(
            drugsGivenDrugId = "id1", drugsGivenAmountGiven = -10
        )
        val drugsGivenAndDrugModel3 = drugsGivenAndDrugsModel().copy(
            drugsGivenDrugId = "id1", drugsGivenAmountGiven = 26
        )
        val drugsGivenAndDrugModel4 = drugsGivenAndDrugsModel().copy(
            drugsGivenDrugId = "id2", drugsGivenAmountGiven = -79
        )
        val drugsGivenAndDrugModel5 = drugsGivenAndDrugsModel().copy(
            drugsGivenDrugId = "id2", drugsGivenAmountGiven = 18
        )
        val drugsGivenAndDrugModel6 = drugsGivenAndDrugsModel().copy(
            drugsGivenDrugId = "id2", drugsGivenAmountGiven = 47
        )

        val listOfDrugs = listOf(
            drugsGivenAndDrugModel1,
            drugsGivenAndDrugModel2,
            drugsGivenAndDrugModel3,
            drugsGivenAndDrugModel4,
            drugsGivenAndDrugModel5,
            drugsGivenAndDrugModel6
        )

        val returnList = CalculateDrugsGiven().invoke(listOfDrugs)

        assertThat(returnList.size).isEqualTo(2)

        val calcDrugsGivenAndDrugModel1 = returnList[0]
        val calcDrugsGivenAndDrugModel2 = returnList[1]

        assertThat(calcDrugsGivenAndDrugModel1.drugsGivenAmountGiven).isEqualTo(46)
        assertThat(calcDrugsGivenAndDrugModel2.drugsGivenAmountGiven).isEqualTo(65)
    }

    @Test
    fun calculateDrugsGiven_totalIsCorrect() {

        val drugsGivenAndDrugModel1 = drugsGivenAndDrugsModel().copy(
            drugsGivenDrugId = "id1", drugsGivenAmountGiven = 20
        )
        val drugsGivenAndDrugModel2 = drugsGivenAndDrugsModel().copy(
            drugsGivenDrugId = "id1", drugsGivenAmountGiven = 10
        )
        val drugsGivenAndDrugModel3 = drugsGivenAndDrugsModel().copy(
            drugsGivenDrugId = "id1", drugsGivenAmountGiven = 26
        )
        val drugsGivenAndDrugModel4 = drugsGivenAndDrugsModel().copy(
            drugsGivenDrugId = "id2", drugsGivenAmountGiven = 79
        )
        val drugsGivenAndDrugModel5 = drugsGivenAndDrugsModel().copy(
            drugsGivenDrugId = "id2", drugsGivenAmountGiven = 18
        )
        val drugsGivenAndDrugModel6 = drugsGivenAndDrugsModel().copy(
            drugsGivenDrugId = "id2", drugsGivenAmountGiven = 47
        )

        val listOfDrugs = listOf(
            drugsGivenAndDrugModel1,
            drugsGivenAndDrugModel2,
            drugsGivenAndDrugModel3,
            drugsGivenAndDrugModel4,
            drugsGivenAndDrugModel5,
            drugsGivenAndDrugModel6
        )

        val returnList = CalculateDrugsGiven().invoke(listOfDrugs)

        val calcDrugsGivenAndDrugModel1 = returnList[0]
        val calcDrugsGivenAndDrugModel2 = returnList[1]

        assertThat(calcDrugsGivenAndDrugModel1.drugsGivenAmountGiven).isEqualTo(56)
        assertThat(calcDrugsGivenAndDrugModel2.drugsGivenAmountGiven).isEqualTo(144)
    }

}