package android.template.ui.navigation

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.navigation.FloatingWindow
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDeepLink
import androidx.navigation.NavDestination
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.Navigator
import androidx.navigation.get

/**
 * Created by yovi.putra on 2/22/24.
 * Copyright (c) 2024 NitipYuk All rights reserved.
 */

@Navigator.Name(BottomSheetNavigator.NAME)
class BottomSheetNavigator : Navigator<BottomSheetNavigator.Destination>() {

    /**
     * Get the back stack from the [state].
     */
    internal val backStack get() = state.backStack

    /**
     * Dismiss the bottom-sheet destination associated with the given [backStackEntry].
     */
    internal fun dismiss(backStackEntry: NavBackStackEntry) {
        state.popWithTransition(backStackEntry, false)
    }

    override fun navigate(
        entries: List<NavBackStackEntry>,
        navOptions: NavOptions?,
        navigatorExtras: Extras?
    ) {
        entries.forEach { entry ->
            state.push(entry)
        }
    }

    override fun createDestination(): Destination {
        return Destination(this) { }
    }

    /**
     * do something before navigation state.popWithTransition execute
     */
    var preparePopBackStack: (onComplete: () -> Unit) -> Unit = {}

    override fun popBackStack(popUpTo: NavBackStackEntry, savedState: Boolean) {
        preparePopBackStack {
            state.popWithTransition(popUpTo, savedState)
        }
    }

    internal fun onTransitionComplete(entry: NavBackStackEntry) {
        state.markTransitionComplete(entry)
    }

    /**
     * NavDestination specific to [BottomSheetNavigator]
     */
    @OptIn(ExperimentalMaterial3Api::class)
    @NavDestination.ClassType(Composable::class)
    class Destination(
        navigator: BottomSheetNavigator,
        internal val properties: BottomSheetProperties = BottomSheetProperties(),
        internal val content: @Composable (NavBackStackEntry) -> Unit
    ) : NavDestination(navigator), FloatingWindow

    internal companion object {
        internal const val NAME = "bottom_sheet"
    }
}

@OptIn(ExperimentalMaterial3Api::class)
fun NavGraphBuilder.bottomSheetComposable(
    route: String,
    arguments: List<NamedNavArgument> = emptyList(),
    deepLinks: List<NavDeepLink> = emptyList(),
    properties: BottomSheetProperties = BottomSheetProperties(),
    content: @Composable (NavBackStackEntry) -> Unit
) {
    addDestination(
        BottomSheetNavigator.Destination(
            provider[BottomSheetNavigator::class],
            properties,
            content
        ).apply {
            this.route = route
            arguments.forEach { (argumentName, argument) ->
                addArgument(argumentName, argument)
            }
            deepLinks.forEach { deepLink ->
                addDeepLink(deepLink)
            }
        }
    )
}
