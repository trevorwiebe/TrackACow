package com.trevorwiebe.trackacow.domain.use_cases.call_use_cases

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isEqualTo
import com.trevorwiebe.trackacow.domain.data_generators.callModel
import com.trevorwiebe.trackacow.domain.data_type_converters.transformCallAndRationModel
import com.trevorwiebe.trackacow.domain.models.compound_model.CallAndRationModel
import com.trevorwiebe.trackacow.domain.repository.local.CallRepository
import com.trevorwiebe.trackacow.domain.repository.local.RationsRepository
import com.trevorwiebe.trackacow.domain.repository.remote.CallRepositoryRemote
import com.trevorwiebe.trackacow.domain.use_cases.CalculateDayStartAndDayEnd
import com.trevorwiebe.trackacow.domain.use_cases.GetCloudDatabaseId
import com.trevorwiebe.trackacow.domain.utils.DataSource
import com.trevorwiebe.trackacow.test_utils.TrackACowAndroidTest
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.runBlocking
import org.junit.Test
import java.util.UUID
import javax.inject.Inject

@HiltAndroidTest
class ReadCallByLotIdAndDateUCTest : TrackACowAndroidTest() {

    private lateinit var readCallByLotIdAndDateUC: ReadCallByLotIdAndDateUC
    private lateinit var createCallUC: CreateCallUC
    private lateinit var updateCallUC: UpdateCallUC

    @Inject
    lateinit var rationRepository: RationsRepository

    @Inject
    lateinit var callRepository: CallRepository

    @Inject
    lateinit var callRepositoryRemote: CallRepositoryRemote

    @Inject
    lateinit var calculateDayStartAndDayEnd: CalculateDayStartAndDayEnd

    @Inject
    lateinit var getCloudDatabaseId: GetCloudDatabaseId

    override fun setUp() {
        super.setUp()
        readCallByLotIdAndDateUC = ReadCallByLotIdAndDateUC(
            rationRepository,
            callRepository,
            callRepositoryRemote,
            context
        )
        createCallUC = CreateCallUC(
            callRepository,
            callRepositoryRemote,
            getCloudDatabaseId,
            context
        )
        updateCallUC = UpdateCallUC(
            callRepository,
            callRepositoryRemote,
            context
        )
    }

    @Test
    fun readCallsNetworkConnected_callIsReturned() = runBlocking {

        val lotId = UUID.randomUUID().toString()
        val timeNow = System.currentTimeMillis()
        val dateList = calculateDayStartAndDayEnd(timeNow)

        val callModel = callModel().copy(
            lotId = lotId,
            date = timeNow
        )
        createCallUC(callModel, true)

        readCallByLotIdAndDateUC(lotId, dateList[0], dateList[1], true).dataFlow.test {

            val returnedData1 = awaitItem()
            val returnedCall1 = returnedData1.first as CallAndRationModel
            val dataSource1 = returnedData1.second

            assertThat(returnedCall1).transformCallAndRationModel().isEqualTo(callModel)
            assertThat(dataSource1).isEqualTo(DataSource.Local)

            val returnedData2 = awaitItem()

            val returnedCall2 = returnedData2.first as CallAndRationModel
            val dataSource2 = returnedData2.second

            assertThat(returnedCall2).transformCallAndRationModel().isEqualTo(callModel)
            assertThat(dataSource2).isEqualTo(DataSource.Cloud)

            val updatedCallModel = callModel.copy(
                callAmount = 45
            )

            updateCallUC(updatedCallModel, true)

            val returnedData3 = awaitItem()
            val returnedCall3 = returnedData3.first as CallAndRationModel
            val dataSource3 = returnedData3.second

            assertThat(returnedCall3).transformCallAndRationModel().isEqualTo(updatedCallModel)
            assertThat(dataSource3).isEqualTo(DataSource.Cloud)
        }
    }

    @Test
    fun readCallsNetworkUnavailable_callIsReturned() = runBlocking {

        val lotId = UUID.randomUUID().toString()
        val timeNow = System.currentTimeMillis()
        val dateList = calculateDayStartAndDayEnd(timeNow)

        val callModel = callModel().copy(
            lotId = lotId,
            date = timeNow
        )
        createCallUC(callModel, false)

        readCallByLotIdAndDateUC(lotId, dateList[0], dateList[1], false).dataFlow.test {

            val returnedData1 = awaitItem()
            val returnedCall1 = returnedData1.first as CallAndRationModel
            val dataSource1 = returnedData1.second

            assertThat(returnedCall1).transformCallAndRationModel().isEqualTo(callModel)
            assertThat(dataSource1).isEqualTo(DataSource.Local)

            val updatedCallModel = callModel.copy(
                callAmount = 45
            )

            updateCallUC(updatedCallModel, false)

            val returnedData2 = awaitItem()
            val returnedCall2 = returnedData2.first as CallAndRationModel
            val dataSource2 = returnedData2.second

            assertThat(returnedCall2).transformCallAndRationModel().isEqualTo(updatedCallModel)
            assertThat(dataSource2).isEqualTo(DataSource.Local)

        }
    }

}