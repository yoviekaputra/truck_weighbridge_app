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
import android.template.core.components.UnifyTextField
import android.template.core.extensions.collectAsStateWithLifecycle
import android.template.core.ui.MyApplicationTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun NewWeighbridgeRoute(
    modifier: Modifier = Modifier,
    viewModel: NewWeighbridgeViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle()

    NewWeighbridgeScreen(
        uiState = uiState.value,
        onEvent = viewModel::onEvent,
        modifier = modifier.padding(16.dp)
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun NewWeighbridgeScreen(
    uiState: NewWeighbridgeUiState,
    onEvent: (NewWeighbridgeUiEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier.wrapContentSize(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        val show = remember { mutableStateOf(false) }
        val datePickerState = rememberDatePickerState(
            uiState.date,
            initialDisplayMode = DisplayMode.Picker
        )
        val timePickerState = rememberTimePickerState(
            initialHour = uiState.hours,
            initialMinute = uiState.minutes
        )

        UnifyDateTimePickerDialog(
            datePickerState = datePickerState,
            timePickerState = timePickerState,
            show = show.value,
            onClosed = {
                show.value = false
                onEvent(NewWeighbridgeUiEvent.OnDateTimeChanged(it.time))
            }
        )

        UnifyTextField(
            label = "Date Time",
            initialValue = uiState.dateTimeFormatted,
            onValueChange = { },
            readOnly = true,
            trailingIcon = {
                IconButton(onClick = { show.value = true }) {
                    Icon(imageVector = Icons.Outlined.Edit, contentDescription = "edit_datetime")
                }
            },
            modifier = Modifier
                .fillMaxWidth()
        )

        UnifyTextField(
            label = "Driver Name",
            initialValue = uiState.driverName,
            onValueChange = { onEvent(NewWeighbridgeUiEvent.OnDriverNameChanged(it)) },
            modifier = Modifier.fillMaxWidth()
        )

        UnifyTextField(
            label = "Licence Number",
            initialValue = uiState.licenceNumber,
            onValueChange = { onEvent(NewWeighbridgeUiEvent.OnLicenceNumberChanged(it)) },
            modifier = Modifier.fillMaxWidth()
        )

        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(text = "Weight")

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                UnifyTextField(
                    label = "Inbound",
                    initialValue = uiState.inboundWeight.toString(),
                    onValueChange = { onEvent(NewWeighbridgeUiEvent.OnInboundWeightChanged(it)) },
                    modifier = Modifier.weight(1f)
                )

                UnifyTextField(
                    label = "Outbound",
                    initialValue = uiState.outboundWeight.toString(),
                    onValueChange = { onEvent(NewWeighbridgeUiEvent.OnOutboundWeightChanged(it)) },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Net Weight", fontWeight = FontWeight.Bold)
            Text(text = uiState.netWeight, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        }

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                onEvent(NewWeighbridgeUiEvent.OnSaveClicked)
            }) {
            Text("Save")
        }
    }
}

// Previews

@Preview(showBackground = true)
@Composable
private fun DefaultPreview() {
    MyApplicationTheme {
        NewWeighbridgeScreen(
            uiState = NewWeighbridgeUiState(),
            onEvent = { _ -> }
        )
    }
}