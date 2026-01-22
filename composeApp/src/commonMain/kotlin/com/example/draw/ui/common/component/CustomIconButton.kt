package com.example.draw.ui.common.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.draw.ui.common.preview.PreviewComponent

@Composable
fun CustomIconButton(
    icon: ImageVector,
    onClick: (() -> Unit)? = null,
) {

    Box(
        modifier = Modifier
            .height(48.dp)
            .width(48.dp)
            .clip(CircleShape)
            .background(if (onClick != null) MaterialTheme.colorScheme.primary else Color.Gray)
            .clickable(
                enabled = onClick != null
            ){
                onClick?.invoke()
            }
    ){
        Icon(
            modifier = Modifier
                .align(Alignment.Center),
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onPrimary
        )
    }


}

@Preview
@Composable
fun CustomIconButtonPreview() {
    PreviewComponent {
        CustomIconButton(
            icon = Icons.Default.Undo,
            onClick = {}
        )
    }
}