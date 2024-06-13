package android.template.core.components

import android.template.core.ui.MyApplicationTheme
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp


@Composable
fun UnifyLoadingView(indicatorSize: Dp, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(modifier = Modifier.size(indicatorSize))
    }
}

@Preview
@Composable
private fun UnifyLoadingViewPreview() {
    MyApplicationTheme {
        UnifyLoadingView(indicatorSize = 24.dp)
    }
}