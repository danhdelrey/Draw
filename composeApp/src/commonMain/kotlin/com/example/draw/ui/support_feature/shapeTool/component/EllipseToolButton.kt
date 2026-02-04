package com.example.draw.ui.support_feature.shapeTool.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Circle
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
 * Button to enter/exit Ellipse Drawing Mode.
 *
 * @param isActive Whether ellipse mode is currently active
 * @param onToggleEllipseMode Callback to toggle ellipse mode
 */
@Composable
fun EllipseToolButton(
    isActive: Boolean,
    onToggleEllipseMode: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(CircleShape)
            .background(if(isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant)
            .width(40.dp)
            .height(40.dp)
            .clickable {
                onToggleEllipseMode()
            },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            modifier = Modifier
                .width(24.dp)
                .height(24.dp),
            imageVector = Icons.Default.Circle,
            tint = if(isActive) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
            contentDescription = null
        )
    }
}

@Preview
@Composable
fun EllipseToolButtonPreview() {
    PreviewComponent {
        EllipseToolButton(
            isActive = false,
            onToggleEllipseMode = {}
        )
    }
}

@Preview
@Composable
fun EllipseToolButtonActivePreview() {
    PreviewComponent {
        EllipseToolButton(
            isActive = true,
            onToggleEllipseMode = {}
        )
    }
}

