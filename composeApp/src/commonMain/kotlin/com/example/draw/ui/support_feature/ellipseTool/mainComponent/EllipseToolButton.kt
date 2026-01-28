package com.example.draw.ui.support_feature.ellipseTool.mainComponent

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
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
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .border(
                width = 5.dp,
                color = if (isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceContainer,
                shape = CircleShape
            )
            .clickable { onToggleEllipseMode() }
    ) {
        // Custom ellipse icon drawn with Canvas
        Canvas(
            modifier = Modifier
                .padding(12.dp)
                .width(24.dp)
                .height(24.dp)
                .align(Alignment.Center)
        ) {
            val iconColor = if (isActive) {
                Color(0xFF6200EE) // Primary color
            } else {
                Color.DarkGray
            }

            // Draw an ellipse icon
            drawOval(
                color = iconColor,
                topLeft = Offset(size.width * 0.1f, size.height * 0.25f),
                size = Size(size.width * 0.8f, size.height * 0.5f),
                style = Stroke(width = 2.dp.toPx())
            )

            // Draw small control points to hint at the tool function
            val dotRadius = 2.dp.toPx()
            // Left dot
            drawCircle(
                color = iconColor,
                radius = dotRadius,
                center = Offset(size.width * 0.1f, size.height * 0.5f)
            )
            // Right dot
            drawCircle(
                color = iconColor,
                radius = dotRadius,
                center = Offset(size.width * 0.9f, size.height * 0.5f)
            )
            // Top dot
            drawCircle(
                color = iconColor,
                radius = dotRadius,
                center = Offset(size.width * 0.5f, size.height * 0.25f)
            )
            // Bottom dot
            drawCircle(
                color = iconColor,
                radius = dotRadius,
                center = Offset(size.width * 0.5f, size.height * 0.75f)
            )
        }
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

