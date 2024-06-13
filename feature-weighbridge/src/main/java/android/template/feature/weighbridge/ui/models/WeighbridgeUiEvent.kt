package android.template.feature.weighbridge.ui.models

sealed interface WeighbridgeUiEvent {

    data class OnDeleteClick(val data: WeighbridgeUiModel) : WeighbridgeUiEvent

    data class OnEditClick(val data: WeighbridgeUiModel) : WeighbridgeUiEvent

    object OnAddClick : WeighbridgeUiEvent

    data class OnSearchChanged(val query: String) : WeighbridgeUiEvent

    object OnSortChanged : WeighbridgeUiEvent
}