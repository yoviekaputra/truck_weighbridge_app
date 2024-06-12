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
import android.template.feature.weighbridge.ui.MyModelUiState.Error
import android.template.feature.weighbridge.ui.MyModelUiState.Loading
import android.template.feature.weighbridge.ui.MyModelUiState.Success
import android.template.feature.weighbridge.ui.WeighbridgeUiModel.Companion.asUiModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class WeighbridgeViewModel @Inject constructor(
    private val myModelRepository: MyModelRepository
) : ViewModel() {

    val uiState: StateFlow<MyModelUiState> = myModelRepository.myModels
        .map<List<WeighbridgeData>, MyModelUiState> { Success(data = it.asUiModel) }
        .catch { emit(Error(it)) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Loading)

    fun onEvent(event: WeighbridgeUiEvent) {

    }
}

sealed interface MyModelUiState {
    object Loading : MyModelUiState
    data class Error(val throwable: Throwable) : MyModelUiState
    data class Success(val data: List<WeighbridgeUiModel>) : MyModelUiState
}

sealed interface WeighbridgeUiEvent {

    data class OnDeleteClick(val data: WeighbridgeUiModel) : WeighbridgeUiEvent

    data class OnEditClick(val data: WeighbridgeUiModel) : WeighbridgeUiEvent
}