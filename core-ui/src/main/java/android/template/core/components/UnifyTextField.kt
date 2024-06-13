package android.template.core.components

import android.template.core.ui.MyApplicationTheme
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview

/**
 * Created by yovi.putra on 6/11/24.
 * Copyright (c) 2024 Multimodule template All rights reserved.
 */

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun UnifyTextField(
    modifier: Modifier = Modifier,
    label: String = "",
    initialValue: String,
    maxLines: Int = 1,
    imeAction: ImeAction = ImeAction.Next,
    readOnly: Boolean = false,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    onValueChange: (String) -> Unit
) {
    TextField(
        label = { Text(text = label) },
        value = initialValue,
        onValueChange = onValueChange,
        maxLines = maxLines,
        keyboardOptions = KeyboardOptions(imeAction = imeAction),
        readOnly = readOnly,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        modifier = modifier.fillMaxWidth()
    )
}

@Preview
@Composable
private fun UnifyTextViewPreview() {

    MyApplicationTheme {
        var value by remember { mutableStateOf("value") }

        UnifyTextField(label = "Label Test", initialValue = value) {
            value = it
        }
    }
}