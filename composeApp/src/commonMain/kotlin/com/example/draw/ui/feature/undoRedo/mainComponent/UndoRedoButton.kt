package com.example.draw.ui.feature.undoRedo.mainComponent

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Redo
import androidx.compose.material.icons.filled.Undo
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.draw.ui.common.component.CustomIconButton
import com.example.draw.ui.common.preview.PreviewComponent

@Composable
fun UndoRedoButton(){
    Row {
        CustomIconButton(
            icon = Icons.Default.Undo,
            onClick = {
                // Handle undo action
            }
        )
        Spacer(Modifier.width(15.dp))
        CustomIconButton(
            icon = Icons.Default.Redo,
            onClick = {
                // Handle undo action
            }
        )
    }
}

@Preview
@Composable
fun UndoRedoButtonPreview(){
    PreviewComponent {
        UndoRedoButton()
    }
}