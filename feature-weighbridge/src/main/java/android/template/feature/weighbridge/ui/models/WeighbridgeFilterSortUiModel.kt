package android.template.feature.weighbridge.ui.models

/**
 * Created by yovi.putra on 6/13/24.
 * Copyright (c) 2024 Multimodule template All rights reserved.
 */

data class WeighbridgeFilterSortUiModel(
    val query: String = "",
    val sortByDate: WeighbridgeFilterSort = WeighbridgeFilterSort.DEFAULT
)

enum class WeighbridgeFilterSort {
    DEFAULT,
    DESC;


    fun next(): WeighbridgeFilterSort {
        return when (this) {
            DEFAULT -> DESC
            DESC -> DEFAULT
        }
    }
}
