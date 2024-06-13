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

import android.template.core.data.MyModelRepository
import android.template.core.data.models.WeighbridgeData
import android.template.feature.weighbridge.ui.models.SearchResultUiState
import android.template.feature.weighbridge.ui.models.SearchResultUiState.Error
import android.template.feature.weighbridge.ui.models.SearchResultUiState.Loading
import android.template.feature.weighbridge.ui.models.SearchResultUiState.Success
import android.template.feature.weighbridge.ui.models.WeighbridgeFilterSort
import android.template.feature.weighbridge.ui.models.WeighbridgeFilterSortUiModel
import android.template.feature.weighbridge.ui.models.WeighbridgeUiEffect
import android.template.feature.weighbridge.ui.models.WeighbridgeUiEvent
import android.template.feature.weighbridge.ui.models.WeighbridgeUiModel.Companion.asData
import android.template.feature.weighbridge.ui.models.WeighbridgeUiModel.Companion.asUiModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
class WeighbridgeViewModel @Inject constructor(
    private val myModelRepository: MyModelRepository
) : ViewModel() {

    private val _filterSortUiState = MutableStateFlow(WeighbridgeFilterSortUiModel())
    val filterSortUiState: StateFlow<WeighbridgeFilterSortUiModel> =
        _filterSortUiState.asStateFlow()

    val searchResultUiState: StateFlow<SearchResultUiState> = _filterSortUiState
        .debounce { if (it.query.isEmpty()) 0 else 500 }
        .flatMapLatest {
            myModelRepository.get(
                query = _filterSortUiState.value.query,
                sortByAscending = _filterSortUiState.value.sortByDate == WeighbridgeFilterSort.DEFAULT
            )
        }.map<List<WeighbridgeData>, SearchResultUiState> {
            Success(data = it.asUiModel)
        }
        .catch { emit(Error(it)) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Loading)

    private val _uiEffect = MutableSharedFlow<WeighbridgeUiEffect>()
    val uiEffect: SharedFlow<WeighbridgeUiEffect> = _uiEffect.asSharedFlow()

    fun onEvent(event: WeighbridgeUiEvent) = viewModelScope.launch {
        when (event) {
            is WeighbridgeUiEvent.OnAddClick -> {
                _uiEffect.emit(WeighbridgeUiEffect.OnCreateTicket)
            }

            is WeighbridgeUiEvent.OnDeleteClick -> {
                myModelRepository.delete(data = event.data.asData)
            }

            is WeighbridgeUiEvent.OnSearchChanged -> {
                onSearchChanged(event.query)
            }

            is WeighbridgeUiEvent.OnSortChanged -> {
                _filterSortUiState.update { it.copy(sortByDate = it.sortByDate.next()) }
            }

            else -> {

            }
        }
    }

    private fun onSearchChanged(query: String) {
        _filterSortUiState.update { it.copy(query = query) }
    }
}






