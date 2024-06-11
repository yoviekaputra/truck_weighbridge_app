package android.template.core.components

import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UnifyTimePickerDialog(
    show: Boolean,
    modifier: Modifier = Modifier,
    state: TimePickerState = rememberTimePickerState(),
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
            TimePicker(state = state)
        }
    }
}