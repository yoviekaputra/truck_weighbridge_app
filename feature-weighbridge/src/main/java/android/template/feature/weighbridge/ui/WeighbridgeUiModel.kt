package android.template.feature.weighbridge.ui

import android.template.core.data.models.WeighbridgeData
import android.template.core.extensions.asDateFormatted
import androidx.compose.runtime.Immutable

/**
 * Created by yovi.putra on 6/12/24.
 * Copyright (c) 2024 Multimodule template All rights reserved.
 */

@Immutable
data class WeighbridgeUiModel(
    val id: Int = 0,
    private val datetime: Long,
    val licenceNumber: String,
    val driverName: String,
    private val inboundWeight: Double,
    private val outboundWeight: Double
) {

    val datetimeText
        get() = datetime.asDateFormatted("dd MMM yyyy HH:mm")

    val inboundWeightText get() = "$inboundWeight"

    val outboundWeightText get() = "$outboundWeight"

    val netWeight get() = "${outboundWeight - inboundWeight}"

    companion object {

        val WeighbridgeData.asUiModel
            get() = WeighbridgeUiModel(
                id = id,
                datetime = datetime,
                licenceNumber = licenceNumber,
                driverName = driverName,
                inboundWeight = inboundWeight,
                outboundWeight = outboundWeight
            )

        val List<WeighbridgeData>.asUiModel
            get() = map { it.asUiModel }

        val WeighbridgeUiModel.asData
            get() = WeighbridgeData(
                id = id,
                datetime = datetime,
                licenceNumber = licenceNumber,
                driverName = driverName,
                inboundWeight = inboundWeight,
                outboundWeight = outboundWeight
            )
    }
}
