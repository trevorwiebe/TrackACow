package com.trevorwiebe.trackacow.domain.use_cases.call_use_cases

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isTrue
import com.trevorwiebe.trackacow.domain.data_type_converters.transformCacheCallModel
import com.trevorwiebe.trackacow.domain.data_type_converters.transformCallModel
import com.trevorwiebe.trackacow.domain.models.call.CallModel
import com.trevorwiebe.trackacow.domain.repository.local.CallRepository
import com.trevorwiebe.trackacow.domain.repository.remote.CallRepositoryRemote
import com.trevorwiebe.trackacow.domain.use_cases.GetCloudDatabaseId
import com.trevorwiebe.trackacow.domain.utils.Constants
import com.trevorwiebe.trackacow.domain.utils.Utility
import com.trevorwiebe.trackacow.test_utils.TrackACowAndroidTest
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.runBlocking
import org.junit.Test
import java.util.UUID
import javax.inject.Inject

@HiltAndroidTest
class CreateCallUCTest : TrackACowAndroidTest() {

    private lateinit var createCallUC: CreateCallUC

    @Inject
    lateinit var callRepository: CallRepository
    @Inject
    lateinit var callRepositoryRemote: CallRepositoryRemote
    @Inject
    lateinit var getCloudId: GetCloudDatabaseId

    override fun setUp() {
        super.setUp()
        createCallUC = CreateCallUC(
            callRepository = callRepository,
            callRepositoryRemote = callRepositoryRemote,
            getCloudDatabaseId = getCloudId,
            context = context
        )
    }

    @Test
    fun insertCallNetworkConnected_checkIfCallInserted() = runBlocking {

        val lotId = UUID.randomUUID().toString()

        val call = CallModel(
            callPrimaryKey = 0,
            callAmount = 50,
            date = System.currentTimeMillis(),
            lotId = lotId,
            callRationId = null,
            callCloudDatabaseId = UUID.randomUUID().toString()
        )

        createCallUC(call, true)

        callRepository.getCalls().test {

            val list = awaitItem()

            assertThat(list.size).isEqualTo(1)
            assertThat(list.first()).transformCallModel().isEqualTo(call)
        }

        callRepositoryRemote.readCallAndRationByLotIdRemote(lotId).test {
            val list = awaitItem()
            val callModelList = list.second
            assertThat(callModelList.size).isEqualTo(1)
            assertThat(callModelList.first()).transformCallModel().isEqualTo(call)
        }

        val dataToUpload = Utility.isThereNewDataToUpload(context)
        assertThat(dataToUpload).isFalse()
    }

    @Test
    fun insertCallNetworkUnavailable_checkIfCallInserted() = runBlocking {


        val lotId = UUID.randomUUID().toString()

        val call = CallModel(
            callPrimaryKey = 0,
            callAmount = 60,
            date = System.currentTimeMillis(),
            lotId = lotId,
            callRationId = null,
            callCloudDatabaseId = UUID.randomUUID().toString()
        )

        createCallUC(call, false)

        callRepository.getCalls().test {

            val list = awaitItem()

            assertThat(list.size).isEqualTo(1)
            assertThat(list.first()).transformCallModel().isEqualTo(call)
        }

        val cacheCallList = callRepository.getCacheCalls()
        assertThat(cacheCallList.size).isEqualTo(1)
        assertThat(cacheCallList.first().whatHappened).isEqualTo(Constants.INSERT_UPDATE)
        assertThat(cacheCallList.first()).transformCacheCallModel().isEqualTo(call)

        val dataToUpload = Utility.isThereNewDataToUpload(context)
        assertThat(dataToUpload).isTrue()

    }

    override fun tearDown() {
        super.tearDown()
        Utility.setNewDataToUpload(context, false)
    }
}