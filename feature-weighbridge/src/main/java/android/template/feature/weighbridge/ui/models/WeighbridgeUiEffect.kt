package android.template.feature.weighbridge.ui.models

sealed interface WeighbridgeUiEffect {

    object OnCreateTicket : WeighbridgeUiEffect

    data class OnEditTicket(val data: WeighbridgeUiModel) : WeighbridgeUiEffect
}