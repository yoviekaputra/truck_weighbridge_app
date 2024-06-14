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
import android.template.core.components.UnifyHeader
import android.template.core.components.UnifyLoadingView
import android.template.core.components.UnifyScaffold
import android.template.core.components.UnifySearchTextField
import android.template.core.data.di.fakeMyModels
import android.template.core.extensions.collectAsStateWithLifecycle
import android.template.core.ui.MyApplicationTheme
import android.template.feature.weighbridge.ui.components.WeighbridgeCard
import android.template.feature.weighbridge.ui.models.SearchResultUiState
import android.template.feature.weighbridge.ui.models.WeighbridgeFilterSort
import android.template.feature.weighbridge.ui.models.WeighbridgeFilterSortUiModel
import android.template.feature.weighbridge.ui.models.WeighbridgeUiEffect
import android.template.feature.weighbridge.ui.models.WeighbridgeUiEvent
import android.template.feature.weighbridge.ui.models.WeighbridgeUiModel
import android.template.feature.weighbridge.ui.models.WeighbridgeUiModel.Companion.asUiModel
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.KeyboardArrowUp
import androidx.compose.material.icons.sharp.KeyboardArrowDown
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
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

    val searchResultUiState = viewModel.searchResultUiState.collectAsStateWithLifecycle()
    val filterSortUiState = viewModel.filterSortUiState.collectAsStateWithLifecycle()
    val lifecycle = LocalLifecycleOwner.current.lifecycle

    LaunchedEffect(key1 = lifecycle, key2 = viewModel) {
        lifecycle.repeatOnLifecycle(state = Lifecycle.State.STARTED) {
            viewModel.uiEffect.collectLatest {
                when (it) {
                    is WeighbridgeUiEffect.OnCreateTicket -> onCreateTicket()
                    is WeighbridgeUiEffect.OnEditTicket -> {

                    }
                }
            }
        }
    }

    MyApplicationTheme {
        WeighbridgeScreen(
            filterSortUiState = filterSortUiState.value,
            searchResultUiState = searchResultUiState.value,
            onEvent = viewModel::onEvent,
            modifier = modifier
        )
    }
}

@Composable
fun WeighbridgeScreen(
    filterSortUiState: WeighbridgeFilterSortUiModel,
    searchResultUiState: SearchResultUiState,
    onEvent: (WeighbridgeUiEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    UnifyScaffold(
        modifier = modifier,
        topBar = {
            UnifyHeader(
                title = "Weighbridge",
                showNavigation = false
            )
        },
        content = { padding ->
            WeighbridgeContent(
                filterSortUiState = filterSortUiState,
                searchResultUiState = searchResultUiState,
                onEvent = onEvent,
                modifier = Modifier.padding(padding)
            )
        },
        floatingActionButton = {
            if (searchResultUiState is SearchResultUiState.Success) {
                FloatingActionButton(
                    onClick = { onEvent(WeighbridgeUiEvent.OnAddClick) },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.inversePrimary,
                    shape = CircleShape
                ) {
                    Icon(Icons.Filled.Add, "Small floating action button.")
                }
            }
        }
    )
}

@Composable
private fun WeighbridgeContent(
    filterSortUiState: WeighbridgeFilterSortUiModel,
    searchResultUiState: SearchResultUiState,
    onEvent: (WeighbridgeUiEvent) -> Unit,
    modifier: Modifier = Modifier
) {

    when (searchResultUiState) {
        is SearchResultUiState.Success -> WeighbridgeList(
            modifier = modifier,
            filterSortUiState = filterSortUiState,
            items = searchResultUiState.data,
            onDeleteClick = {
                onEvent(WeighbridgeUiEvent.OnDeleteClick(it))
            },
            onEditClick = {
                onEvent(WeighbridgeUiEvent.OnEditClick(it))
            },
            onSearchChanged = {
                onEvent(WeighbridgeUiEvent.OnSearchChanged(it))
            },
            onSortChanged = {
                onEvent(WeighbridgeUiEvent.OnSortChanged)
            }
        )

        is SearchResultUiState.Error -> {
            UnifyErrorView(
                modifier = modifier.fillMaxSize(),
                errorMessage = searchResultUiState.throwable.message.orEmpty()
            )
        }

        is SearchResultUiState.Loading -> {
            UnifyLoadingView(
                modifier = modifier.fillMaxSize(),
                size = 24.dp
            )
        }
    }
}

@Composable
internal fun WeighbridgeList(
    filterSortUiState: WeighbridgeFilterSortUiModel,
    items: List<WeighbridgeUiModel>,
    onEditClick: (WeighbridgeUiModel) -> Unit,
    onDeleteClick: (WeighbridgeUiModel) -> Unit,
    onSearchChanged: (String) -> Unit,
    onSortChanged: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            UnifySearchTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                searchQuery = filterSortUiState.query,
                onSearchQueryChanged = onSearchChanged,
            )

            IconButton(
                modifier = Modifier,
                onClick = onSortChanged
            ) {
                val icon = when (filterSortUiState.sortByDate) {
                    WeighbridgeFilterSort.DEFAULT -> Icons.Outlined.KeyboardArrowUp
                    WeighbridgeFilterSort.DESC -> Icons.Sharp.KeyboardArrowDown
                }
                Icon(imageVector = icon, contentDescription = "")
            }
        }
        LazyColumn {
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
}


@Preview
@Composable
private fun WeighbridgePreview() {
    MyApplicationTheme {
        WeighbridgeScreen(
            filterSortUiState = WeighbridgeFilterSortUiModel(),
            searchResultUiState = SearchResultUiState.Success(
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
            filterSortUiState = WeighbridgeFilterSortUiModel(),
            searchResultUiState = SearchResultUiState.Loading,
            onEvent = {}
        )
    }
}

@Preview
@Composable
private fun WeighbridgeErrorPreview() {
    MyApplicationTheme {
        WeighbridgeScreen(
            filterSortUiState = WeighbridgeFilterSortUiModel(),
            searchResultUiState = SearchResultUiState.Error(Throwable("Error")),
            onEvent = {}
        )
    }
}