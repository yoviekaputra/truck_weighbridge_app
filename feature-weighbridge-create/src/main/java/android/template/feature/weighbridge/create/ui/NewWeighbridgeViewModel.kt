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

import android.template.core.data.MyModelRepository
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewWeighbridgeViewModel @Inject constructor(
    private val myModelRepository: MyModelRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(NewWeighbridgeUiState())
    val uiState: StateFlow<NewWeighbridgeUiState> get() = _uiState.asStateFlow()


    fun onEvent(event: NewWeighbridgeUiEvent) {
        when (event) {
            is NewWeighbridgeUiEvent.OnDateTimeChanged -> _uiState.update {
                it.copy(dateTime = event.value.toLongOrNull() ?: 0)
            }
            is NewWeighbridgeUiEvent.OnDriverNameChanged -> _uiState.update {
                it.copy(driverName = event.value)
            }
            is NewWeighbridgeUiEvent.OnLicenceNumberChanged -> _uiState.update {
                it.copy(licenceNumber = event.value)
            }
            is NewWeighbridgeUiEvent.OnInboundWeightChanged -> _uiState.update {
                it.copy(inboundWeight = event.value.toDoubleOrNull() ?: 0.0)
            }
            is NewWeighbridgeUiEvent.OnOutboundWeightChanged -> _uiState.update {
                it.copy(outboundWeight = event.value.toDoubleOrNull() ?: 0.0)
            }
            is NewWeighbridgeUiEvent.OnSaveClicked -> {

            }
        }

    }

    fun addMyModel(name: String) {
        viewModelScope.launch {
            myModelRepository.add(name)
        }
    }
}

sealed interface MyModelUiState {
    object Loading : MyModelUiState
    data class Error(val throwable: Throwable) : MyModelUiState
    data class Success(val data: List<String>) : MyModelUiState
}
