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
import android.template.core.data.models.WeighbridgeData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
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

    private val _uiEffect = MutableSharedFlow<NewWeighbridgeUiEffect>()
    val uiEffect: MutableSharedFlow<NewWeighbridgeUiEffect> get() = _uiEffect


    fun onEvent(event: NewWeighbridgeUiEvent) {
        when (event) {
            is NewWeighbridgeUiEvent.OnDateTimeChanged -> _uiState.update {
                it.copy(date = event.value)
            }

            is NewWeighbridgeUiEvent.OnDriverNameChanged -> _uiState.update {
                it.copy(driverName = event.value)
            }

            is NewWeighbridgeUiEvent.OnLicenceNumberChanged -> _uiState.update {
                it.copy(licenceNumber = event.value)
            }

            is NewWeighbridgeUiEvent.OnInboundWeightChanged -> _uiState.update {
                it.copy(inboundWeight = event.value)
            }

            is NewWeighbridgeUiEvent.OnOutboundWeightChanged -> _uiState.update {
                it.copy(outboundWeight = event.value)
            }

            is NewWeighbridgeUiEvent.OnSaveClicked -> prepareToSave()
        }

    }

    private fun prepareToSave() = viewModelScope.launch {
        _uiState.update { it.copy(isLoading = true) }

        val result = fieldValidation()

        if (result.isBlank()) {
            processSave()
        } else {
            _uiState.update { it.copy(isLoading = false, errorMessage = result) }
        }
    }

    private fun processSave() = viewModelScope.launch {
        val state = _uiState.value
        val newData = WeighbridgeData(
            datetime = state.date,
            driverName = state.driverName,
            licenceNumber = state.licenceNumber,
            inboundWeight = state.inboundWeight.toDoubleOrNull() ?: 0.0,
            outboundWeight = state.outboundWeight.toDoubleOrNull() ?: 0.0
        )

        runCatching {
            myModelRepository.add(newData)
        }.onSuccess {
            _uiEffect.emit(NewWeighbridgeUiEffect.OnSavedSuccess)
        }.onFailure {
            _uiEffect.emit(NewWeighbridgeUiEffect.OnSaveError(message = it.message.orEmpty()))
        }
    }

    private fun fieldValidation() = with(_uiState.value) {
        val message = mutableListOf<String>()

        driverName.ifBlank { message.add("Driver Name") }
        licenceNumber.ifBlank { message.add("Licence Number") }
        inboundWeight.ifBlank { message.add("Inbound Weight") }
        outboundWeight.ifBlank { message.add("Outbound Weight") }

        if (message.isEmpty()) {
            ""
        } else {
            message.joinToString(separator = ", ") + " is required"
        }
    }
}

sealed interface MyModelUiState {
    object Loading : MyModelUiState
    data class Error(val throwable: Throwable) : MyModelUiState
    data class Success(val data: List<String>) : MyModelUiState
}

sealed interface NewWeighbridgeUiEffect {

    data class OnSaveError(val message: String) : NewWeighbridgeUiEffect

    object OnSavedSuccess : NewWeighbridgeUiEffect
}