package android.template.feature.weighbridge.create.ui

import androidx.compose.runtime.Immutable

/**
 * Created by yovi.putra on 6/11/24.
 * Copyright (c) 2024 Multimodule template All rights reserved.
 */

@Immutable
data class NewWeighbridgeUiState(
    val dateTime: Long = 0L,
    val licenceNumber: String = "",
    val driverName: String = "",
    val inboundWeight: Double = 0.0,
    val outboundWeight: Double = 0.0
) {

    val netWeight
        get() = (outboundWeight - inboundWeight).toString()
}
