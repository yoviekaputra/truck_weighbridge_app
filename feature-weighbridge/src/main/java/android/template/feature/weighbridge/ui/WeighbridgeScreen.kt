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

import android.template.core.components.UnifyErrorView
import android.template.core.components.UnifyLoadingView
import android.template.core.components.UnifyScaffold
import android.template.core.extensions.collectAsStateWithLifecycle
import android.template.core.ui.MyApplicationTheme
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

    MyApplicationTheme {
        UnifyScaffold(
            title = "Weighbrige",
            content = {
                WeighbridgeScreen(
                    uiState = uiState.value,
                    onEvent = viewModel::onEvent,
                    modifier = Modifier.padding(it)
                )
            }
        )
    }
}

@Composable
fun WeighbridgeScreen(
    uiState: MyModelUiState,
    onEvent: (WeighbridgeUiEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    when (uiState) {
        is MyModelUiState.Success -> WeighbridgeList(
            modifier = modifier,
            items = uiState.data,
            onDeleteClick = {
                onEvent(WeighbridgeUiEvent.OnDeleteClick(it))
            }, onEditClick = {
                onEvent(WeighbridgeUiEvent.OnEditClick(it))
            }
        )

        is MyModelUiState.Error -> {
            UnifyErrorView(errorMessage = uiState.throwable.message.orEmpty())
        }
        is MyModelUiState.Loading -> {
            UnifyLoadingView(indicatorSize = 24.dp)
        }
    }
}

@Composable
internal fun WeighbridgeList(
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
        WeighbridgeScreen(
            uiState = MyModelUiState.Loading,
            onEvent = {}
        )
    }
}