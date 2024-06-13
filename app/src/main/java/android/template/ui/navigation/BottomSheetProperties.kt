package android.template.ui.navigation

import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.safeContent
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetValue
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp

/**
 * Created by yovi.putra on 2/24/24.
 * Copyright (c) 2024 NitipYuk All rights reserved.
 */

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Immutable
data class BottomSheetProperties(
    val skipPartiallyExpanded: Boolean = false,
    val confirmValueChange: (SheetValue) -> Boolean = { true },
    val skipHiddenState: Boolean = false,
    val shape: @Composable () -> Shape = { BottomSheetDefaults.ExpandedShape },
    val containerColor: @Composable () -> Color = { BottomSheetDefaults.ContainerColor },
    val contentColor: @Composable () -> Color = { contentColorFor(containerColor()) },
    val tonalElevation: Dp = BottomSheetDefaults.Elevation,
    val scrimColor: @Composable () -> Color = { BottomSheetDefaults.ScrimColor },
    val dragHandle: @Composable (() -> Unit)? = { BottomSheetDefaults.DragHandle() },
    val windowInsets: @Composable () -> WindowInsets = { WindowInsets.safeContent }
)