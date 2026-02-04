package com.example.draw.ui.support_feature.shapeTool.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CropSquare
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.draw.ui.common.preview.PreviewComponent

/**
 * Button to enter/exit Rectangle Drawing Mode.
 *
 * @param isActive Whether rectangle mode is currently active
 * @param onToggleRectangleMode Callback to toggle rectangle mode
 */
@Composable
fun RectangleToolButton(
    isActive: Boolean,
    onToggleRectangleMode: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(CircleShape)
            .background(if(isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant)
            .width(40.dp)
            .height(40.dp)
            .clickable {
                onToggleRectangleMode()
            },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            modifier = Modifier
                .width(24.dp)
                .height(24.dp),
            imageVector = Icons.Default.CropSquare,
            tint = if(isActive) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
            contentDescription = null
        )
    }
}

@Preview
@Composable
fun RectangleToolButtonPreview() {
    PreviewComponent {
        RectangleToolButton(
            isActive = false,
            onToggleRectangleMode = {}
        )
    }
}

@Preview
@Composable
fun RectangleToolButtonActivePreview() {
    PreviewComponent {
        RectangleToolButton(
            isActive = true,
            onToggleRectangleMode = {}
        )
    }
}

