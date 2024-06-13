package android.template.core.components

import android.template.core.ui.MyApplicationTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp


@Composable
fun UnifyLoadingView(
    modifier: Modifier = Modifier,
    size: Dp,
    inverseColor: Boolean = false
) {
    Box(
        modifier = modifier, contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            modifier = modifier.requiredSize(size),
            strokeWidth = 2.dp,
            color = if (inverseColor) {
                Color.White
            } else {
                ProgressIndicatorDefaults.circularColor
            },
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun UnifyLoadingViewPreview() {
    MyApplicationTheme {
        UnifyLoadingView(modifier = Modifier.fillMaxSize(), size = 100.dp)
    }
}