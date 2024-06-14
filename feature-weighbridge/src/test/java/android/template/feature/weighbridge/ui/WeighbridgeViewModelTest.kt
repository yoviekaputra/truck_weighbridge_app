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


import android.template.core.data.di.FakeWeighbridgeRepository
import android.template.feature.weighbridge.ui.models.SearchResultUiState
import android.template.feature.weighbridge.ui.models.WeighbridgeFilterSort
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@OptIn(ExperimentalCoroutinesApi::class)
class WeighbridgeViewModelTest {

    @Test
    fun uiState_initiallyLoading() = runTest {
        val viewModel = WeighbridgeViewModel(FakeWeighbridgeRepository())
        assertEquals(viewModel.searchResultUiState.first(), SearchResultUiState.Loading)
        assertEquals(viewModel.uiEffect.first(), SearchResultUiState.Loading)
        assertEquals(viewModel.filterSortUiState.first().query, "")
        assertEquals(viewModel.filterSortUiState.first().sortByDate, WeighbridgeFilterSort.DEFAULT)
    }

    @Test
    fun uiState_onItemSaved_isDisplayed() = runTest {
        val viewModel = WeighbridgeViewModel(FakeWeighbridgeRepository())

        assertEquals(viewModel.searchResultUiState.first(), SearchResultUiState.Loading)
    }
}

