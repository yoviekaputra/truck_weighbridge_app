/*
 * Copyright (C) 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package android.template.feature.weighbridge.ui

import android.template.core.data.di.fakeMyModels
import android.template.core.extensions.collectAsStateWithLifecycle
import android.template.core.ui.MyApplicationTheme
import android.template.feature.weighbridge.ui.WeighbridgeUiModel.Companion.asUiModel
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun WeighbridgeRoute(
    modifier: Modifier = Modifier,
    viewModel: WeighbridgeViewModel = hiltViewModel()
) {

    val uiState = viewModel.uiState.collectAsStateWithLifecycle()

    when (val state = uiState.value) {
        is MyModelUiState.Success -> WeighbridgeListScreen(
            items = state.data,
            onDeleteClick = {
                viewModel.onEvent(WeighbridgeUiEvent.OnDeleteClick(it))
            }, onEditClick = {
                viewModel.onEvent(WeighbridgeUiEvent.OnEditClick(it))
            }
        )
        is MyModelUiState.Error -> {}
        is MyModelUiState.Loading -> {

        }
    }
}

@Composable
internal fun WeighbridgeListScreen(
    items: List<WeighbridgeUiModel>,
    onEditClick: (WeighbridgeUiModel) -> Unit,
    onDeleteClick: (WeighbridgeUiModel) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(modifier = modifier) {
        items(items = items, key = { it.id }) {
            WeighbridgeCard(
                data = it,
                onEditClick = onEditClick,
                onDeleteClick = onDeleteClick,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }
    }
}


@Preview
@Composable
private fun WeighbridgePreview() {
    MyApplicationTheme {
        WeighbridgeListScreen(
            items = fakeMyModels.asUiModel,
            onDeleteClick = { _ -> },
            onEditClick = { _ -> }
        )
    }
}