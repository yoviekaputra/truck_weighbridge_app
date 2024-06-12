package android.template.core.data.models

import android.template.core.database.WeighbridgeEntity

/**
 * Created by yovi.putra on 6/12/24.
 * Copyright (c) 2024 Multimodule template All rights reserved.
 */

data class WeighbridgeData(
    var id: Int = 0,
    val datetime: Long,
    val licenceNumber: String,
    val driverName: String,
    val inboundWeight: Double,
    val outboundWeight: Double
) {

    companion object {
        val WeighbridgeData.asEntity
            get() = WeighbridgeEntity(
                datetime = datetime,
                licenceNumber = licenceNumber,
                driverName = driverName,
                inboundWeight = inboundWeight,
                outboundWeight = outboundWeight
            ).apply {
                uid = id
            }
    }
}

val WeighbridgeEntity.asData
    get() = WeighbridgeData(
        id = uid,
        datetime = datetime,
        licenceNumber = licenceNumber,
        driverName = driverName,
        inboundWeight = inboundWeight,
        outboundWeight = outboundWeight
    )