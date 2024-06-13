package android.template.core.components

import android.template.core.ui.MyApplicationTheme
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

/**
 * Created by yovi.putra on 08/02/24"
 * Project name: NitipYuk
 **/

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UnifyHeader(
    title: String,
    showNavigation: Boolean = true,
    onNavigationClick: () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {}
) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = title,
                modifier = Modifier.padding(start = 8.dp),
                color = Color.White
            )
        },
        navigationIcon = {
            if (showNavigation) {
                IconButton(
                    modifier = Modifier
                        .size(48.dp),
                    onClick = onNavigationClick,
                    content = {
                        Icon(
                            imageVector = Icons.Rounded.ArrowBack,
                            contentDescription = null,
                            tint = Color.White
                        )
                    }
                )
            }
        },
        actions = actions,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
        )
    )
}

@Preview
@Composable
private fun UnifyHeaderPreview() {
    MyApplicationTheme {
        UnifyHeader(
            title = "Title",
            showNavigation = true,
            onNavigationClick = {}
        )
    }
}