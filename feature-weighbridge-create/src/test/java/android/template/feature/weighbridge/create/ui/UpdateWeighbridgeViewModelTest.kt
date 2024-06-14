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
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
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
class UpdateWeighbridgeViewModelTest {

    @RelaxedMockK
    lateinit var weighbridgeRepository: WeighbridgeRepository

    @get:Rule
    val coroutineScopeRule = UnconfinedTestRule()

    private lateinit var viewModel: NewWeighbridgeViewModel

    private val data = WeighbridgeData(
        id = 1,
        datetime = Calendar.getInstance().timeInMillis,
        driverName = "driverName",
        licenceNumber = "licenceNumber",
        inboundWeight = 100.0,
        outboundWeight = 150.0
    )

    @Before
    fun beforeTest() {
        MockKAnnotations.init(this)
    }

    @Test
    fun uiEvent_OnLoadData_Success() = runTest {
        // [GIVEN]
        every { weighbridgeRepository.get(id = any()) } returns flowOf(data)
        val savedState = SavedStateHandle(mapOf("id" to data.id))
        viewModel = NewWeighbridgeViewModel(weighbridgeRepository, savedState)

        // [THEN]
        val uiState = viewModel.uiState.value
        assertFalse(uiState.isLoading)
        assertTrue(uiState.shouldEdit)
        assertEquals(data.driverName, uiState.driverName)
        assertEquals(data.licenceNumber, uiState.licenceNumber)
        assertEquals(data.inboundWeight.toString(), uiState.inboundWeight)
        assertEquals(data.outboundWeight.toString(), uiState.outboundWeight)
        assertEquals("50", uiState.netWeight)
        assertTrue(uiState.errorMessage.isBlank())

        verify { weighbridgeRepository.get(id = any()) }
    }

    @Test
    fun uiEvent_OnLoadData_Error() = runTest {
        // [GIVEN]
        val error = "Data not found"
        every { weighbridgeRepository.get(id = any()) } throws Throwable(error)
        val savedState = SavedStateHandle(mapOf("id" to data.id))
        viewModel = NewWeighbridgeViewModel(weighbridgeRepository, savedState)

        // [THEN]
        val uiState = viewModel.uiState.value
        assertTrue(uiState.isLoading)
        assertEquals(error, uiState.errorMessage)

        verify { weighbridgeRepository.get(id = any()) }
    }

    @Test
    fun uiEvent_OnSaveData_Success() = runTest {
        // [GIVEN]
        every { weighbridgeRepository.get(id = any()) } returns flowOf(data)
        coEvery { weighbridgeRepository.update(any()) } returns Unit

        val savedState = SavedStateHandle(mapOf("id" to data.id))
        viewModel = NewWeighbridgeViewModel(weighbridgeRepository, savedState)

        val uiEffects = mutableListOf<NewWeighbridgeUiEffect>()
        backgroundScope.launch(UnconfinedTestDispatcher()) {
            viewModel.uiEffect.toList(uiEffects)
        }

        // [WHEN]
        viewModel.onEvent(NewWeighbridgeUiEvent.OnOutboundWeightChanged("1000"))
        viewModel.onEvent(NewWeighbridgeUiEvent.OnInboundWeightChanged("100"))
        viewModel.onEvent(NewWeighbridgeUiEvent.OnSaveClicked)

        // [THEN]
        val uiState = viewModel.uiState.value
        assertTrue(uiState.shouldEdit)
        assertEquals("1000", uiState.outboundWeight)
        assertEquals("100", uiState.inboundWeight)
        assertEquals("900", uiState.netWeight)
        assertTrue(uiState.errorMessage.isBlank())
        assertTrue(uiEffects.first() is NewWeighbridgeUiEffect.OnSavedSuccess)

        verify { weighbridgeRepository.get(id = any()) }
        coVerify { weighbridgeRepository.update(any()) }
    }

    @Test
    fun uiEvent_OnSaveData_Failed() = runTest {
        // [GIVEN]
        val error = "Error occurred"
        every { weighbridgeRepository.get(id = any()) } returns flowOf(data)
        coEvery { weighbridgeRepository.update(any()) } throws Throwable(error)

        val savedState = SavedStateHandle(mapOf("id" to data.id))
        viewModel = NewWeighbridgeViewModel(weighbridgeRepository, savedState)

        // [WHEN]
        viewModel.onEvent(NewWeighbridgeUiEvent.OnSaveClicked)

        // [THEN]
        val uiState = viewModel.uiState.value
        assertTrue(uiState.shouldEdit)
        assertEquals(error, uiState.errorMessage)

        verify { weighbridgeRepository.get(id = any()) }
        coVerify { weighbridgeRepository.update(any()) }
    }
}

