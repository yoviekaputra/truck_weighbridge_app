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

import android.template.core.components.UnifyDateTimePickerDialog
import android.template.core.components.UnifyLoadingView
import android.template.core.components.UnifyTextField
import android.template.core.extensions.collectAsStateWithLifecycle
import android.template.core.ui.MyApplicationTheme
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.flow.collectLatest

@Composable
fun NewWeighbridgeRoute(
    onClosePage: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: NewWeighbridgeViewModel = hiltViewModel()
) {
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val uiState = viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = lifecycle, key2 = viewModel) {
        lifecycle.repeatOnLifecycle(state = Lifecycle.State.STARTED) {
            viewModel.uiEffect.collectLatest {
                when (it) {
                    is NewWeighbridgeUiEffect.OnSavedSuccess,
                    is NewWeighbridgeUiEffect.OnLoadedDataError -> onClosePage()
                }
            }
        }
    }

    MyApplicationTheme {
        NewWeighbridgeScreen(
            uiState = uiState.value, onEvent = viewModel::onEvent, modifier = modifier
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun NewWeighbridgeScreen(
    uiState: NewWeighbridgeUiModel,
    onEvent: (NewWeighbridgeUiEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    val bottomPadding = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

    Column(
        modifier = modifier
            .padding(bottom = bottomPadding)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {

        StickyError(error = uiState.errorMessage)

        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            val show = remember { mutableStateOf(false) }
            val datePickerState = rememberDatePickerState(
                uiState.date, initialDisplayMode = DisplayMode.Picker
            )
            val timePickerState = rememberTimePickerState(
                initialHour = uiState.hours, initialMinute = uiState.minutes
            )

            UnifyDateTimePickerDialog(datePickerState = datePickerState,
                timePickerState = timePickerState,
                show = show.value,
                onClosed = {
                    show.value = false
                    onEvent(NewWeighbridgeUiEvent.OnDateTimeChanged(it.time))
                })

            UnifyTextField(
                label = "Date Time",
                initialValue = uiState.dateTimeFormatted,
                onValueChange = {
                },
                readOnly = true,
                trailingIcon = {
                    IconButton(onClick = { show.value = true }) {
                        Icon(
                            imageVector = Icons.Outlined.Edit,
                            contentDescription = "edit_datetime"
                        )
                    }
                },
                enabled = uiState.shouldEdit.not(),
                modifier = Modifier.fillMaxWidth()
            )

            UnifyTextField(
                label = "Driver Name",
                initialValue = uiState.driverName,
                onValueChange = { onEvent(NewWeighbridgeUiEvent.OnDriverNameChanged(it)) },
                enabled = uiState.shouldEdit.not(),
                modifier = Modifier.fillMaxWidth()
            )

            UnifyTextField(
                label = "Licence Number",
                initialValue = uiState.licenceNumber,
                onValueChange = { onEvent(NewWeighbridgeUiEvent.OnLicenceNumberChanged(it)) },
                enabled = uiState.shouldEdit.not(),
                modifier = Modifier.fillMaxWidth()
            )

            Weight(
                inboundWeight = uiState.inboundWeight,
                outboundHeight = uiState.outboundWeight,
                onInboundChanged = { onEvent(NewWeighbridgeUiEvent.OnInboundWeightChanged(it)) },
                onOutboundChanged = { onEvent(NewWeighbridgeUiEvent.OnOutboundWeightChanged(it)) }
            )

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Net Weight", fontWeight = FontWeight.Bold)
                Text(
                    text = uiState.netWeight, fontWeight = FontWeight.Bold, fontSize = 18.sp
                )
            }
        }

        SaveButton(
            isLoading = uiState.isLoading,
            modifier = Modifier.padding(horizontal = 16.dp),
            onClick = {
                onEvent(NewWeighbridgeUiEvent.OnSaveClicked)
            }
        )
    }
}

@Composable
private fun StickyError(error: String, modifier: Modifier = Modifier) {
    AnimatedVisibility(
        enter = fadeIn() + expandVertically(expandFrom = Alignment.Top),
        visible = error.isNotBlank()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = modifier
                .background(Color.Red)
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Notifications,
                contentDescription = "warning icon",
                tint = Color.White
            )

            Text(text = error, color = Color.White, fontSize = 14.sp)
        }
    }
}

@Composable
private fun SaveButton(isLoading: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Button(
        modifier = modifier.fillMaxWidth(),
        onClick = onClick,
        enabled = !isLoading
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        )
        {
            if (isLoading) {
                UnifyLoadingView(modifier = Modifier, inverseColor = true, size = 24.dp)
            }

            Text("Save")
        }
    }
}

@Composable
private fun Weight(
    inboundWeight: String,
    outboundHeight: String,
    onInboundChanged: (String) -> Unit,
    onOutboundChanged: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(text = "Weight")

        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            UnifyTextField(
                label = "Inbound",
                initialValue = inboundWeight,
                onValueChange = onInboundChanged,
                modifier = Modifier.weight(1f)
            )

            UnifyTextField(
                label = "Outbound",
                initialValue = outboundHeight,
                onValueChange = onOutboundChanged,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

// Previews

@Preview(showBackground = true)
@Composable
private fun DefaultPreview() {
    MyApplicationTheme {
        NewWeighbridgeScreen(uiState = NewWeighbridgeUiModel(), onEvent = { _ -> })
    }
}

@Preview(showBackground = true)
@Composable
private fun ErrorPreview() {
    MyApplicationTheme {
        NewWeighbridgeScreen(
            uiState = NewWeighbridgeUiModel(
            errorMessage = "Error Message"
        ), onEvent = { _ -> })
    }
}