/*
 * Copyright (C) 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package android.template.feature.weighbridge.create.ui


import android.template.core.data.WeighbridgeRepository
import android.template.core.data.models.WeighbridgeData
import android.template.core.ut.UnconfinedTestRule
import androidx.lifecycle.SavedStateHandle
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.RelaxedMockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.Calendar

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@OptIn(ExperimentalCoroutinesApi::class)
class NewWeighbridgeViewModelTest {

    @RelaxedMockK
    lateinit var weighbridgeRepository: WeighbridgeRepository

    @get:Rule
    val coroutineScopeRule = UnconfinedTestRule()

    private lateinit var viewModel: NewWeighbridgeViewModel

    @Before
    fun beforeTest() {
        MockKAnnotations.init(this)

        val savedState = SavedStateHandle(mapOf("id" to 0))
        viewModel = NewWeighbridgeViewModel(weighbridgeRepository, savedState)
    }

    @Test
    fun uiEvent_IntentCreateNew() = runTest {
        val uiState = viewModel.uiState.value
        assertFalse(uiState.shouldEdit)
        assertFalse(uiState.isLoading)
        assertTrue(uiState.driverName.isBlank())
        assertTrue(uiState.licenceNumber.isBlank())
        assertTrue(uiState.inboundWeight.isBlank())
        assertTrue(uiState.outboundWeight.isBlank())
        assertEquals("0", uiState.netWeight)
        assertTrue(uiState.errorMessage.isBlank())
    }

    @Test
    fun uiEvent_OnDateTimeChanged() = runTest {
        val uiState = { viewModel.uiState.value }
        val datetime = Calendar.getInstance().apply {
            set(Calendar.YEAR, 2024)
            set(Calendar.MONTH, 6 - 1)
            set(Calendar.DAY_OF_MONTH, 14)
            set(Calendar.HOUR_OF_DAY, 18)
            set(Calendar.MINUTE, 27)
        }

        viewModel.onEvent(NewWeighbridgeUiEvent.OnDateTimeChanged(datetime.timeInMillis))

        assertEquals("14-Jun-2024 18:27", uiState().dateTimeFormatted)
        assertFalse(uiState().shouldEdit)
        assertFalse(uiState().isLoading)
        assertTrue(uiState().driverName.isBlank())
        assertTrue(uiState().licenceNumber.isBlank())
        assertTrue(uiState().inboundWeight.isBlank())
        assertTrue(uiState().outboundWeight.isBlank())
        assertEquals("0", uiState().netWeight)
        assertTrue(uiState().errorMessage.isBlank())
    }

    @Test
    fun uiEvent_OnDriverNameChanged() = runTest {
        val uiState = { viewModel.uiState.value }
        val valueChanged = "Test"

        viewModel.onEvent(NewWeighbridgeUiEvent.OnDriverNameChanged(valueChanged))

        assertEquals("Test", uiState().driverName)
        assertFalse(uiState().shouldEdit)
        assertFalse(uiState().isLoading)
    }

    @Test
    fun uiEvent_OnLicenceNumberChanged() = runTest {
        val uiState = { viewModel.uiState.value }
        val valueChanged = "Test"

        viewModel.onEvent(NewWeighbridgeUiEvent.OnLicenceNumberChanged(valueChanged))

        assertEquals("Test", uiState().licenceNumber)
        assertFalse(uiState().shouldEdit)
        assertFalse(uiState().isLoading)
    }

    @Test
    fun uiEvent_OnInboundWeightChanged() = runTest {
        val uiState = { viewModel.uiState.value }
        val valueChanged = "100"

        viewModel.onEvent(NewWeighbridgeUiEvent.OnInboundWeightChanged(valueChanged))

        assertEquals("100", uiState().inboundWeight)
        assertEquals("-100", uiState().netWeight)
        assertFalse(uiState().shouldEdit)
        assertFalse(uiState().isLoading)
    }

    @Test
    fun uiEvent_OnOutboundWeightChanged() = runTest {
        val uiState = { viewModel.uiState.value }
        val valueChanged = "100"

        viewModel.onEvent(NewWeighbridgeUiEvent.OnOutboundWeightChanged(valueChanged))

        assertEquals("100", uiState().outboundWeight)
        assertEquals("100", uiState().netWeight)
        assertFalse(uiState().shouldEdit)
        assertFalse(uiState().isLoading)
    }

    @Test
    fun uiEvent_OnSaveClicked_Success() = runTest {
        val data = WeighbridgeData(
            id = 1,
            datetime = Calendar.getInstance().timeInMillis,
            driverName = "driverName",
            licenceNumber = "licenceNumber",
            inboundWeight = 100.0,
            outboundWeight = 150.0
        )
        val uiState = { viewModel.uiState.value }
        val uiEffects = mutableListOf<NewWeighbridgeUiEffect>()
        backgroundScope.launch(UnconfinedTestDispatcher()) {
            viewModel.uiEffect.toList(uiEffects)
        }

        // [GIVEN]
        coEvery { weighbridgeRepository.add(any()) } returns Unit

        viewModel.onEvent(NewWeighbridgeUiEvent.OnDateTimeChanged(data.datetime))
        viewModel.onEvent(NewWeighbridgeUiEvent.OnDriverNameChanged(data.driverName))
        viewModel.onEvent(NewWeighbridgeUiEvent.OnLicenceNumberChanged(data.licenceNumber))
        viewModel.onEvent(NewWeighbridgeUiEvent.OnInboundWeightChanged(data.inboundWeight.toString()))
        viewModel.onEvent(NewWeighbridgeUiEvent.OnOutboundWeightChanged(data.outboundWeight.toString()))

        // [WHEN]
        viewModel.onEvent(NewWeighbridgeUiEvent.OnSaveClicked)

        // [THEN]
        assertTrue(uiState().errorMessage.isBlank())
        assertEquals("50", uiState().netWeight)
        assertTrue(uiEffects.first() is NewWeighbridgeUiEffect.OnSavedSuccess)
        coVerify { weighbridgeRepository.add(any()) }
    }

    @Test
    fun uiEvent_OnSaveClicked_FieldError() = runTest {
        val data = WeighbridgeData(
            id = 1,
            datetime = Calendar.getInstance().timeInMillis,
            driverName = "driverName",
            licenceNumber = "licenceNumber",
            inboundWeight = 100.0,
            outboundWeight = 150.0
        )
        val uiState = { viewModel.uiState.value }
        val expectedError =
            "Driver Name, Licence Number, Inbound Weight, Outbound Weight is required"

        // [GIVEN]
        coEvery { weighbridgeRepository.add(any()) } returns Unit

        viewModel.onEvent(NewWeighbridgeUiEvent.OnDateTimeChanged(data.datetime))
        viewModel.onEvent(NewWeighbridgeUiEvent.OnDriverNameChanged(""))
        viewModel.onEvent(NewWeighbridgeUiEvent.OnLicenceNumberChanged(""))
        viewModel.onEvent(NewWeighbridgeUiEvent.OnInboundWeightChanged(""))
        viewModel.onEvent(NewWeighbridgeUiEvent.OnOutboundWeightChanged(""))

        // [WHEN]
        viewModel.onEvent(NewWeighbridgeUiEvent.OnSaveClicked)

        // [THEN]
        assertEquals(expectedError, uiState().errorMessage)
        assertEquals("0", uiState().netWeight)
        coVerify(inverse = true) { weighbridgeRepository.add(any()) }
    }

    @Test
    fun uiEvent_OnSaveClicked_Failed() = runTest {
        val data = WeighbridgeData(
            id = 1,
            datetime = Calendar.getInstance().timeInMillis,
            driverName = "driverName",
            licenceNumber = "licenceNumber",
            inboundWeight = 100.0,
            outboundWeight = 150.0
        )
        val uiState = { viewModel.uiState.value }
        val expectedError = "Error occurred"

        // [GIVEN]
        coEvery { weighbridgeRepository.add(any()) } throws Throwable(expectedError)

        viewModel.onEvent(NewWeighbridgeUiEvent.OnDateTimeChanged(data.datetime))
        viewModel.onEvent(NewWeighbridgeUiEvent.OnDriverNameChanged(data.driverName))
        viewModel.onEvent(NewWeighbridgeUiEvent.OnLicenceNumberChanged(data.licenceNumber))
        viewModel.onEvent(NewWeighbridgeUiEvent.OnInboundWeightChanged(data.inboundWeight.toString()))
        viewModel.onEvent(NewWeighbridgeUiEvent.OnOutboundWeightChanged(data.outboundWeight.toString()))

        // [WHEN]
        viewModel.onEvent(NewWeighbridgeUiEvent.OnSaveClicked)

        // [THEN]
        assertEquals(expectedError, uiState().errorMessage)
        assertEquals("50", uiState().netWeight)
        coVerify { weighbridgeRepository.add(any()) }
    }
}

