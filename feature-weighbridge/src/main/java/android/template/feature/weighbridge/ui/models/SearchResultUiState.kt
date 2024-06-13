package android.template.feature.weighbridge.ui.models

sealed interface SearchResultUiState {
    object Loading : SearchResultUiState
    data class Error(val throwable: Throwable) : SearchResultUiState
    data class Success(val data: List<WeighbridgeUiModel>) : SearchResultUiState
}