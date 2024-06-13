package android.template.ui.navigation

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavDestination
import androidx.navigation.NavHostController
import androidx.navigation.Navigator
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.compose.DialogNavigator

/**
 * Created by yovi.putra on 2/22/24.
 * Copyright (c) 2024 NitipYuk All rights reserved.
 */

/**
 * replicate from [androidx.navigation.compose.rememberNavController]
 */

@Composable
fun rememberNavigationController(
    vararg navigators: Navigator<out NavDestination>
): NavHostController {
    val context = LocalContext.current
    return rememberSaveable(inputs = navigators, saver = navControllerSaver(context)) {
        createNavController(context)
    }.apply {
        for (navigator in navigators) {
            navigatorProvider.addNavigator(navigator)
        }
    }
}


private fun createNavController(context: Context) =
    NavHostController(context).apply {
        navigatorProvider.addNavigator(ComposeNavigator())
        navigatorProvider.addNavigator(DialogNavigator())
        navigatorProvider.addNavigator(BottomSheetNavigator())
    }

/**
 * Saver to save and restore the NavController across config change and process death.
 */
private fun navControllerSaver(
    context: Context
): Saver<NavHostController, *> = Saver(
    save = { it.saveState() },
    restore = { createNavController(context).apply { restoreState(it) } }
)
