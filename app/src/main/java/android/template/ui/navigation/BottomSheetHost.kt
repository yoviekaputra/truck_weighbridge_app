package android.template.ui.navigation

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.SaveableStateHolder
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDestination
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.Navigator
import androidx.navigation.compose.LocalOwnersProvider
import androidx.navigation.compose.NavHost
import androidx.navigation.get
import kotlinx.coroutines.launch

/**
 * Created by yovi.putra on 2/22/24.
 * Copyright (c) 2024 NitipYuk All rights reserved.
 */


/**
 * Show each [BottomSheetNavigator.Destination] on the [BottomSheetNavigator]'s back stack as a BottomSheet.
 *
 * Note
 * - that [NavHost] will call this for you; you do not need to call it manually.
 * - this is replicate from [androidx.navigation.compose.DialogHost]
 */
@Composable
fun BottomSheetHost(navigator: BottomSheetNavigator) {
    val saveableStateHolder = rememberSaveableStateHolder()
    val bottomSheetBackStack by navigator.backStack.collectAsState()
    val visibleBackStack = rememberVisibleList(bottomSheetBackStack)
    visibleBackStack.PopulateVisibleList(bottomSheetBackStack)

    visibleBackStack.forEach { backStackEntry ->
        DisposableEffect(backStackEntry) {
            onDispose {
                navigator.onTransitionComplete(backStackEntry)
            }
        }

        BottomSheetContent(
            backStackEntry = backStackEntry,
            saveableStateHolder = saveableStateHolder,
            navigator = navigator
        )
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun BottomSheetContent(
    backStackEntry: NavBackStackEntry,
    saveableStateHolder: SaveableStateHolder,
    navigator: BottomSheetNavigator
) {
    val destination = backStackEntry.destination as BottomSheetNavigator.Destination
    val properties = destination.properties
    val scope = rememberCoroutineScope()
    val state = rememberModalBottomSheetState(
        skipPartiallyExpanded = properties.skipPartiallyExpanded,
        confirmValueChange = properties.confirmValueChange
    )

    /**
     * before popBackStack, we need close bottom sheet with their animation
     * so hide to bottom sheet using their sheetState before call popBackStack
     */
    navigator.preparePopBackStack = {
        scope.launch {
            state.hide()
        }.invokeOnCompletion {
            it()
        }
    }

    // while in the scope of the composable, we provide the navBackStackEntry as the
    // ViewModelStoreOwner and LifecycleOwner
    backStackEntry.LocalOwnersProvider(saveableStateHolder) {
        ModalBottomSheet(
            onDismissRequest = {
                navigator.dismiss(backStackEntry)
            },
            sheetState = state,
            shape = properties.shape(),
            containerColor = properties.containerColor(),
            contentColor = properties.contentColor(),
            tonalElevation = properties.tonalElevation,
            scrimColor = properties.scrimColor(),
            dragHandle = properties.dragHandle
        ) {
            destination.content(backStackEntry)
        }
    }
}

@Composable
internal fun MutableList<NavBackStackEntry>.PopulateVisibleList(
    transitionsInProgress: Collection<NavBackStackEntry>
) {
    transitionsInProgress.forEach { entry ->
        val lifecycle = entry.getLifecycle()
        DisposableEffect(lifecycle) {
            val observer = LifecycleEventObserver { _, event ->
                // ON_START -> add to visibleBackStack, ON_STOP -> remove from visibleBackStack
                if (event == Lifecycle.Event.ON_START) {
                    // We want to treat the visible lists as Sets but we want to keep
                    // the functionality of mutableStateListOf() so that we recompose in response
                    // to adds and removes.
                    if (!contains(entry)) {
                        add(entry)
                    }
                }
                if (event == Lifecycle.Event.ON_STOP) {
                    remove(entry)
                }
            }
            lifecycle.addObserver(observer)
            onDispose {
                lifecycle.removeObserver(observer)
            }
        }
    }
}

@Composable
internal fun rememberVisibleList(transitionsInProgress: Collection<NavBackStackEntry>) =
    remember(transitionsInProgress) {
        mutableStateListOf<NavBackStackEntry>().also {
            it.addAll(
                transitionsInProgress.filter { entry ->
                    entry.getLifecycle().currentState.isAtLeast(Lifecycle.State.STARTED)
                }
            )
        }
    }


@Composable
fun NavigationHost(
    navController: NavHostController,
    startDestination: String,
    modifier: Modifier = Modifier,
    route: String? = null,
    builder: NavGraphBuilder.() -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
        route = route,
        builder = builder
    )

    NavigationBottomSheetHost(navController = navController)
}

@Composable
private fun NavigationBottomSheetHost(
    navController: NavHostController
) {
    val navigator = navController.navigatorProvider.get<Navigator<out NavDestination>>(
        BottomSheetNavigator.NAME
    ) as? BottomSheetNavigator ?: return

    // Show any bottom-sheet destinations
    BottomSheetHost(navigator)
}