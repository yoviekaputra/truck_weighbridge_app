package android.template.core.components

import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier

/**
 * Created by yovi.putra on 6/11/24.
 * Copyright (c) 2024 Multimodule template All rights reserved.
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UnifyDatePickerDialog(
    show: Boolean,
    modifier: Modifier = Modifier,
    state: DatePickerState = rememberDatePickerState(
        initialSelectedDateMillis = System.currentTimeMillis()
    ),
    onDismissRequest: () -> Unit = {},
) {
    var showState by remember(show) { mutableStateOf(show) }

    if (showState) {
        DatePickerDialog(
            onDismissRequest = onDismissRequest,
            confirmButton = {
                TextButton(
                    onClick = {
                        showState = false
                        onDismissRequest()
                    }
                ) { Text("OK") }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showState = false
                        onDismissRequest()
                    }
                ) { Text("Cancel") }
            },
            modifier = modifier
        )
        {
            DatePicker(state = state)
        }
    }
}