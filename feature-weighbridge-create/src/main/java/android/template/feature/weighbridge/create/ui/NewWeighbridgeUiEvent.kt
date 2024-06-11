package android.template.feature.weighbridge.create.ui

/**
 * Created by yovi.putra on 6/11/24.
 * Copyright (c) 2024 Multimodule template All rights reserved.
 */


sealed class NewWeighbridgeUiEvent {
    data class OnInboundWeightChanged(val value: String) : NewWeighbridgeUiEvent()

    data class OnOutboundWeightChanged(val value: String) : NewWeighbridgeUiEvent()

    data class OnDateTimeChanged(val value: Long) : NewWeighbridgeUiEvent()

    data class OnDriverNameChanged(val value: String) : NewWeighbridgeUiEvent()

    data class OnLicenceNumberChanged(val value: String) : NewWeighbridgeUiEvent()

    object OnSaveClicked : NewWeighbridgeUiEvent()
}