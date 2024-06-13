package android.template.core.components

import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import java.util.Calendar
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UnifyDateTimePickerDialog(
    modifier: Modifier = Modifier,
    show: Boolean,
    datePickerState: DatePickerState = rememberDatePickerState(),
    timePickerState: TimePickerState = rememberTimePickerState(),
    onClosed: (Date) -> Unit = {}
) {
    var showDatePicker by remember(show) { mutableStateOf(show) }
    var showTimePicker by remember { mutableStateOf(false) }
    var dateTime by remember {
        mutableStateOf(
            datePickerState.selectedDateMillis ?: System.currentTimeMillis()
        )
    }

    UnifyDatePickerDialog(
        modifier = modifier,
        show = showDatePicker,
        state = datePickerState,
        onDismissRequest = {
            showDatePicker = false
            showTimePicker = true
            dateTime = datePickerState.selectedDateMillis ?: 0
        }
    )

    UnifyTimePickerDialog(
        modifier = modifier,
        show = showTimePicker,
        state = timePickerState,
        onDismissRequest = {
            showTimePicker = false
            Calendar.getInstance().apply {
                timeInMillis = dateTime
                set(Calendar.HOUR_OF_DAY, timePickerState.hour)
                set(Calendar.MINUTE, timePickerState.minute)
                dateTime = timeInMillis
            }.also {
                onClosed(it.time)
            }
        }
    )
}