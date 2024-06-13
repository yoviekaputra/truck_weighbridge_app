package android.template.core.components

import android.template.core.ui.MyApplicationTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun UnifyErrorView(
    errorMessage: String,
    modifier: Modifier = Modifier,
    onRetry: () -> Unit = {}
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(16.dp)
        ) {
            Text(errorMessage, color = Color.Red)

            Button(onClick = onRetry) {
                Icon(imageVector = Icons.Filled.Refresh, contentDescription = errorMessage)
                Text(text = "Retry")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun UnifyErrorViewPreview() {
    MyApplicationTheme {
        UnifyErrorView(errorMessage = "Error message...")
    }
}