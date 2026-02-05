package com.example.draw.ui.support_feature.text.mainComponent

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.TextFormat
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.draw.ui.common.component.button.ElegantButton
import com.example.draw.ui.common.preview.PreviewComponent
import com.example.draw.ui.support_feature.text.component.TextFieldDialog


@Composable
fun AddTextButton(
    onConfirmText: (String) -> Unit = {}
) {
    var showTextFieldDialog by remember { mutableStateOf(false) }

    if(showTextFieldDialog){
        TextFieldDialog(
            onDismissRequest = {
                showTextFieldDialog = false
            },
            onConfirm = { text ->
                onConfirmText(text)
                showTextFieldDialog = false
            }
        )
    }
    ElegantButton(
        icon = Icons.Default.TextFields,
        onClick = {
            showTextFieldDialog = true
        }
    )
}

@Preview
@Composable
fun AddTextButtonPreview() {
    PreviewComponent {
        AddTextButton(

        )
    }
}

