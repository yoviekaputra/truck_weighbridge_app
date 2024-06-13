package android.template.core.components

import android.template.core.ui.MyApplicationTheme
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

/**
 * Created by yovi.putra on 08/02/24"
 * Project name: NitipYuk
 **/

@Composable
fun UnifyScaffold(
    title: String,
    content: @Composable (PaddingValues) -> Unit,
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState = rememberSnackbarHostState(),
    showNavigation: Boolean = false,
    onNavigationClick: () -> Unit = {},
    floatingActionButton: @Composable () -> Unit = {},
    floatingActionButtonPosition: FabPosition = FabPosition.End,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            UnifyHeader(
                title = title,
                onNavigationClick = onNavigationClick,
                showNavigation = showNavigation
            )
        },
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier.padding(bottom = 52.dp)
            )
        },
        floatingActionButton = floatingActionButton,
        floatingActionButtonPosition = floatingActionButtonPosition,
        content = content
    )
}

@Composable
fun rememberSnackbarHostState() = remember {
    SnackbarHostState()
}

@Preview
@Composable
private fun UnifyScaffoldPreview() {
    MyApplicationTheme {
        UnifyScaffold(
            title = "Title",
            content = {

            }
        )
    }
}