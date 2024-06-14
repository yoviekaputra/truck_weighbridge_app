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
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewWeighbridgeViewModel @Inject constructor(
    private val weighbridgeRepository: WeighbridgeRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    @OptIn(ExperimentalCoroutinesApi::class)
    private val editedTicket = savedStateHandle.getStateFlow("id", 0)
        .filter { it != 0 }
        .flatMapLatest { weighbridgeRepository.get(id = it) }
        .map<WeighbridgeData, EditedTicketUiState> { EditedTicketUiState.Success(it) }
        .catch { emit(EditedTicketUiState.Error("Data not found")) }

    private val _uiState = MutableStateFlow(NewWeighbridgeUiModel())
    val uiState: StateFlow<NewWeighbridgeUiModel> get() = _uiState.asStateFlow()

    private val _uiEffect = MutableSharedFlow<NewWeighbridgeUiEffect>()
    val uiEffect: MutableSharedFlow<NewWeighbridgeUiEffect> get() = _uiEffect

    init {
        viewModelScope.launch {
            editedTicket.collectLatest { state ->
                when (state) {
                    is EditedTicketUiState.Error -> {
                        _uiState.update {
                            it.copy(isLoading = true, errorMessage = state.message)
                        }
                        delay(1000)
                        uiEffect.emit(NewWeighbridgeUiEffect.OnLoadedDataError(state.message))
                    }

                    is EditedTicketUiState.Success -> {
                        val weighbridge = state.data
                        _uiState.update {
                            it.copy(
                                shouldEdit = true,
                                id = weighbridge.id,
                                driverName = weighbridge.driverName,
                                licenceNumber = weighbridge.licenceNumber,
                                inboundWeight = weighbridge.inboundWeight.toString(),
                                outboundWeight = weighbridge.outboundWeight.toString(),
                                date = weighbridge.datetime
                            )
                        }
                    }
                }
            }
        }
    }

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
        val data = WeighbridgeData(
            id = state.id,
            datetime = state.date,
            driverName = state.driverName,
            licenceNumber = state.licenceNumber,
            inboundWeight = state.inboundWeight.toDoubleOrNull() ?: 0.0,
            outboundWeight = state.outboundWeight.toDoubleOrNull() ?: 0.0
        )

        runCatching {
            if (data.id == 0) {
                save(data = data)
            } else {
                edit(data = data)
            }
        }.onSuccess {
            _uiEffect.emit(NewWeighbridgeUiEffect.OnSavedSuccess)
        }.onFailure { t ->
            _uiState.update { it.copy(isLoading = false, errorMessage = t.message.orEmpty()) }
        }
    }

    private suspend fun save(data: WeighbridgeData) {
        weighbridgeRepository.add(data = data)
    }

    private suspend fun edit(data: WeighbridgeData) {
        weighbridgeRepository.update(data = data)
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

sealed interface EditedTicketUiState {
    data class Error(val message: String) : EditedTicketUiState
    data class Success(val data: WeighbridgeData) : EditedTicketUiState
}

sealed interface NewWeighbridgeUiEffect {
    data class OnLoadedDataError(val message: String) : NewWeighbridgeUiEffect

    object OnSavedSuccess : NewWeighbridgeUiEffect
}