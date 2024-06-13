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
import android.template.core.data.di.fakeMyModels
import android.template.core.extensions.collectAsStateWithLifecycle
import android.template.core.ui.MyApplicationTheme
import android.template.feature.weighbridge.ui.WeighbridgeUiModel.Companion.asUiModel
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.flow.collectLatest

@Composable
fun WeighbridgeRoute(
    modifier: Modifier = Modifier,
    onCreateTicket: () -> Unit,
    viewModel: WeighbridgeViewModel = hiltViewModel()
) {

    val uiState = viewModel.uiState.collectAsStateWithLifecycle()
    val lifecycle = LocalLifecycleOwner.current.lifecycle

    LaunchedEffect(key1 = lifecycle, key2 = viewModel) {
        lifecycle.repeatOnLifecycle(state = Lifecycle.State.STARTED) {
            viewModel.uiEffect.collectLatest {
                when (it) {
                    is WeighbridgeUiEffect.OnCreateTicket -> onCreateTicket()
                    else -> {

                    }
                }
            }
        }
    }

    MyApplicationTheme {
        WeighbridgeScreen(uiState = uiState.value, onEvent = viewModel::onEvent, modifier)
    }
}

@Composable
fun WeighbridgeScreen(
    uiState: MyModelUiState,
    onEvent: (WeighbridgeUiEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    UnifyScaffold(
        title = "Weighbridge",
        content = { padding ->
            val modifierContent = modifier.padding(padding)

            when (uiState) {
                is MyModelUiState.Success -> WeighbridgeList(
                    modifier = modifierContent,
                    items = uiState.data,
                    onDeleteClick = {
                        onEvent(WeighbridgeUiEvent.OnDeleteClick(it))
                    },
                    onEditClick = {
                        onEvent(WeighbridgeUiEvent.OnEditClick(it))
                    }
                )

                is MyModelUiState.Error -> {
                    UnifyErrorView(
                        modifier = modifierContent.fillMaxSize(),
                        errorMessage = uiState.throwable.message.orEmpty()
                    )
                }

                is MyModelUiState.Loading -> {
                    UnifyLoadingView(
                        modifier = modifierContent.fillMaxSize(),
                        indicatorSize = 24.dp
                    )
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onEvent(WeighbridgeUiEvent.OnAddClick) },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.inversePrimary,
                shape = CircleShape
            ) {
                Icon(Icons.Filled.Add, "Small floating action button.")
            }
        }
    )
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
            uiState = MyModelUiState.Success(
                fakeMyModels.asUiModel
            ),
            onEvent = {}
        )
    }
}

@Preview
@Composable
private fun WeighbridgeLoadingPreview() {
    MyApplicationTheme {
        WeighbridgeScreen(
            uiState = MyModelUiState.Loading,
            onEvent = {}
        )
    }
}

@Preview
@Composable
private fun WeighbridgeErrorPreview() {
    MyApplicationTheme {
        WeighbridgeScreen(
            uiState = MyModelUiState.Error(Throwable("Error")),
            onEvent = {}
        )
    }
}