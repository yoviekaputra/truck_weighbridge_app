package android.template.feature.weighbridge.create.ui

import android.template.core.extensions.asDateFormatted
import android.template.core.extensions.asTimeOfHours
import android.template.core.extensions.asTimeOfMinutes
import android.template.core.extensions.currentDateTime
import androidx.compose.runtime.Immutable
import java.text.NumberFormat
import java.util.Locale

/**
 * Created by yovi.putra on 6/11/24.
 * Copyright (c) 2024 Multimodule template All rights reserved.
 */

@Immutable
data class NewWeighbridgeUiModel(
    val id: Int = 0,
    val date: Long = currentDateTime.time,
    val hours: Int = date.asTimeOfHours,
    val minutes: Int = date.asTimeOfMinutes,
    val licenceNumber: String = "",
    val driverName: String = "",
    val inboundWeight: String = "",
    val outboundWeight: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String = "",
    val shouldEdit: Boolean = false
) {

    val netWeight: String
        get() {
            val out = outboundWeight.toDoubleOrNull() ?: 0.0
            val `in` = inboundWeight.toDoubleOrNull() ?: 0.0
            val net = out - `in`
            return NumberFormat.getNumberInstance(Locale("id")).format(net)
        }

    val dateTimeFormatted: String
        get() = date.asDateFormatted("dd-MMM-yyyy HH:mm")
}
