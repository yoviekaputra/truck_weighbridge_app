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

package android.template.feature.weighbridge.ui


import android.template.core.data.WeighbridgeRepository
import android.template.core.data.di.fakeMyModels
import android.template.core.data.di.getFakeMyModel
import android.template.core.ut.UnconfinedTestRule
import android.template.feature.weighbridge.ui.models.SearchResultUiState
import android.template.feature.weighbridge.ui.models.WeighbridgeFilterSort
import android.template.feature.weighbridge.ui.models.WeighbridgeUiEffect
import android.template.feature.weighbridge.ui.models.WeighbridgeUiEvent
import android.template.feature.weighbridge.ui.models.WeighbridgeUiModel.Companion.asUiModel
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@OptIn(ExperimentalCoroutinesApi::class)
class WeighbridgeViewModelTest {

    @RelaxedMockK
    lateinit var weighbridgeRepository: WeighbridgeRepository

    @get:Rule
    val coroutineScopeRule = UnconfinedTestRule()

    private lateinit var viewModel: WeighbridgeViewModel

    @Before
    fun beforeTest() {
        MockKAnnotations.init(this)

        viewModel = WeighbridgeViewModel(weighbridgeRepository)
    }

    @Test
    fun uiEvent_initiallyLoading() = runTest {
        assertEquals(SearchResultUiState.Loading, viewModel.searchResultUiState.first())
        assertEquals("", viewModel.filterSortUiState.first().query)
        assertEquals(WeighbridgeFilterSort.DEFAULT, viewModel.filterSortUiState.first().sortByDate)
    }

    @Test
    fun uiEvent_onFirstLoad() = runTest {
        // [GIVEN]
        val query = ""
        every { weighbridgeRepository.get(any(), any()) } returns flowOf(fakeMyModels)

        // [WHEN]
        viewModel.onEvent(event = WeighbridgeUiEvent.OnSearchChanged(query))

        // [THEN]
        val success = viewModel.searchResultUiState.first() as? SearchResultUiState.Success
        assertEquals(fakeMyModels.first().id, success?.data?.first()?.id)

        val filterSort = viewModel.filterSortUiState.first()
        assertEquals(query, filterSort.query)
        assertEquals(WeighbridgeFilterSort.DEFAULT, filterSort.sortByDate)

        verify { weighbridgeRepository.get(any(), any()) }
    }

    @Test
    fun uiEvent_onSortClicked() = runTest {
        // [GIVEN]
        val query = ""
        every { weighbridgeRepository.get(any(), any()) } returns flowOf(fakeMyModels.reversed())

        // [WHEN]
        viewModel.onEvent(event = WeighbridgeUiEvent.OnSortChanged)

        // [THEN]
        val success = viewModel.searchResultUiState.first() as? SearchResultUiState.Success
        assertEquals(fakeMyModels.last().id, success?.data?.first()?.id)

        val filterSort = viewModel.filterSortUiState.first()
        assertEquals(query, filterSort.query)
        assertEquals(WeighbridgeFilterSort.DESC, filterSort.sortByDate)

        verify { weighbridgeRepository.get(any(), any()) }
    }

    @Test
    fun uiEvent_onQueryChanged() = runTest {
        // [GIVEN]
        val fakeId = 3
        val query = "Driver $fakeId"
        val fakeModels = listOf(getFakeMyModel(fakeId))
        val result = mutableListOf<SearchResultUiState>()

        val job = backgroundScope.launch(UnconfinedTestDispatcher()) {
            viewModel.searchResultUiState.toList(result)
        }

        every { weighbridgeRepository.get(any(), any()) } returns flowOf(fakeModels)

        // [WHEN]
        viewModel.onEvent(event = WeighbridgeUiEvent.OnSearchChanged(query))
        advanceUntilIdle()

        // [THEN]
        assertTrue(result.first() is SearchResultUiState.Loading)

        val success = result.last() as? SearchResultUiState.Success
        assertEquals(fakeId, success?.data?.first()?.id)

        val filterSort = viewModel.filterSortUiState.first()
        assertEquals(query, filterSort.query)
        assertEquals(WeighbridgeFilterSort.DEFAULT, filterSort.sortByDate)

        verify { weighbridgeRepository.get(any(), any()) }
        job.cancel()
    }

    @Test
    fun uiEvent_onQueryChangedAndSort() = runTest {
        // [GIVEN]
        val fakeId = 3
        val query = "Driver $fakeId"
        val fakeModels = listOf(getFakeMyModel(fakeId))
        val result = mutableListOf<SearchResultUiState>()

        val job = backgroundScope.launch(UnconfinedTestDispatcher()) {
            viewModel.searchResultUiState.toList(result)
        }

        every { weighbridgeRepository.get(any(), any()) } returns flowOf(fakeModels)

        // [WHEN]
        viewModel.onEvent(event = WeighbridgeUiEvent.OnSearchChanged(query))
        viewModel.onEvent(event = WeighbridgeUiEvent.OnSortChanged)
        advanceUntilIdle()

        // [THEN]
        assertTrue(result.first() is SearchResultUiState.Loading)

        val success = result.last() as? SearchResultUiState.Success
        assertEquals(fakeId, success?.data?.first()?.id)

        val filterSort = viewModel.filterSortUiState.first()
        assertEquals(query, filterSort.query)
        assertEquals(WeighbridgeFilterSort.DESC, filterSort.sortByDate)

        verify { weighbridgeRepository.get(any(), any()) }
        job.cancel()
    }

    @Test
    fun uiEvent_OnEditClick() = runTest {
        // [GIVEN]
        val fakeModel = getFakeMyModel(0)
        val result = mutableListOf<WeighbridgeUiEffect>()

        backgroundScope.launch(UnconfinedTestDispatcher()) {
            viewModel.uiEffect.toList(result)
        }

        // [WHEN]
        viewModel.onEvent(event = WeighbridgeUiEvent.OnEditClick(fakeModel.asUiModel))

        // [THEN]
        val editEffect = result[0] as? WeighbridgeUiEffect.OnEditTicket
        assertEquals(fakeModel.id, editEffect?.id)
    }

    @Test
    fun uiEvent_OnDeleteClick() = runTest {
        // [GIVEN]
        val fakeModel = getFakeMyModel(0)
        coEvery { weighbridgeRepository.delete(any()) } returns Unit

        // [WHEN]
        viewModel.onEvent(event = WeighbridgeUiEvent.OnDeleteClick(fakeModel.asUiModel))

        // [THEN]
        coVerify { weighbridgeRepository.delete(any()) }
    }
}

