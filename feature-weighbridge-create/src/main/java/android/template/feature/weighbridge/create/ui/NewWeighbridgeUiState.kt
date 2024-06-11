package android.template.feature.weighbridge.create.ui

import android.icu.util.Calendar
import android.template.core.extensions.asDateFormatted
import android.template.core.extensions.asTimeOfHours
import android.template.core.extensions.asTimeOfMinutes
import android.template.core.extensions.currentDateTime
import androidx.compose.runtime.Immutable

/**
 * Created by yovi.putra on 6/11/24.
 * Copyright (c) 2024 Multimodule template All rights reserved.
 */

@Immutable
data class NewWeighbridgeUiState(
    val date: Long = currentDateTime.time,
    val hours: Int = date.asTimeOfHours,
    val minutes: Int = date.asTimeOfMinutes,
    val licenceNumber: String = "",
    val driverName: String = "",
    val inboundWeight: Double = 0.0,
    val outboundWeight: Double = 0.0
) {

    val dateTimeFormatted: String
        get() = date.asDateFormatted("dd-MMM-yyyy HH:mm")

    val netWeight
        get() = (outboundWeight - inboundWeight).toString()
}
