package com.example.draw.ui.support_feature.undoRedo.mainComponent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Redo
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.draw.ui.common.preview.PreviewComponent

@Composable
fun RedoButton(
    onRedo: (() -> Unit)? = null,
){
    Box(
        modifier = Modifier
            .clip(CircleShape)
            .background(if(onRedo != null) MaterialTheme.colorScheme.outlineVariant else Color.Transparent)
            .width(40.dp)
            .height(40.dp)
            .clickable(
                enabled = onRedo != null
            ){
                onRedo?.invoke()
            },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            modifier = Modifier
                .width(24.dp)
                .height(24.dp),
            imageVector = Icons.Default.Redo,
            tint = if(onRedo != null) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.outlineVariant,
            contentDescription = null
        )
    }
}

@Preview
@Composable
fun RedoButtonPreview(){
    PreviewComponent {
        RedoButton(
            onRedo = {}
        )
    }
}

@Preview
@Composable
fun DisabledRedoButtonPreview(){
    PreviewComponent {
        RedoButton()
    }
}