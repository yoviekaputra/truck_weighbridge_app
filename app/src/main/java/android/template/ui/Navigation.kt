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

package android.template.ui

import android.template.feature.weighbridge.create.ui.NewWeighbridgeRoute
import android.template.feature.weighbridge.ui.WeighbridgeRoute
import android.template.ui.navigation.BottomSheetProperties
import android.template.ui.navigation.NavigationHost
import android.template.ui.navigation.bottomSheetComposable
import android.template.ui.navigation.rememberNavigationController
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainNavigation() {
    val navController = rememberNavigationController()

    NavigationHost(navController = navController, startDestination = "main") {
        composable("main") {
            WeighbridgeRoute(
                onCreateTicket = { navController.navigate("add") },
                onEditTicket = {
                    navController.navigate("edit/${it}")
                }
            )
        }

        bottomSheetComposable(
            route = "add",
            properties = BottomSheetProperties(skipPartiallyExpanded = true)
        ) {
            NewWeighbridgeRoute(onClosePage = { navController.popBackStack() })
        }

        bottomSheetComposable(
            route = "edit/{id}",
            arguments = listOf(navArgument("id") { type = NavType.IntType }),
            properties = BottomSheetProperties(skipPartiallyExpanded = true)
        ) {
            NewWeighbridgeRoute(onClosePage = { navController.popBackStack() })
        }
    }
}
