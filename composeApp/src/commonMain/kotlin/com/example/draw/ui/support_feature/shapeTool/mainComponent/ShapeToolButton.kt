package com.example.draw.ui.support_feature.shapeTool.mainComponent

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.CropSquare
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.draw.ui.common.component.CustomBottomSheet
import com.example.draw.ui.common.preview.PreviewComponent

enum class ShapeType {
    ELLIPSE, RECTANGLE
}

/**
 * Button to select a Shape Drawing Mode via a Bottom Sheet.
 *
 * @param activeShape The currently active shape type, or null if none.
 * @param onShapeSelected Callback when a shape is selected from the sheet.
 */
@Composable
fun ShapeToolButton(
    activeShape: ShapeType?,
    onShapeSelected: (ShapeType) -> Unit
) {
    var showBottomSheet by remember { mutableStateOf(false) }

    val activeIcon = when (activeShape) {
        ShapeType.ELLIPSE -> Icons.Default.Circle
        ShapeType.RECTANGLE -> Icons.Default.CropSquare
        null -> Icons.Default.Category
    }

    val isActive = activeShape != null

    Box(
        modifier = Modifier
            .clip(CircleShape)
            .background(if (isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant)
            .width(40.dp)
            .height(40.dp)
            .clickable {
                showBottomSheet = true
            },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            modifier = Modifier
                .width(24.dp)
                .height(24.dp),
            imageVector = activeIcon,
            tint = if (isActive) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
            contentDescription = null
        )
    }

    if (showBottomSheet) {
        CustomBottomSheet(
            onDismissRequest = { showBottomSheet = false }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Select Shape",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Ellipse Option
                    ShapeOption(
                        icon = Icons.Default.Circle,
                        label = "Ellipse",
                        isSelected = activeShape == ShapeType.ELLIPSE,
                        onClick = {
                            onShapeSelected(ShapeType.ELLIPSE)
                            showBottomSheet = false
                        }
                    )

                    Spacer(modifier = Modifier.width(24.dp))

                    // Rectangle Option
                    ShapeOption(
                        icon = Icons.Default.CropSquare,
                        label = "Rectangle",
                        isSelected = activeShape == ShapeType.RECTANGLE,
                        onClick = {
                            onShapeSelected(ShapeType.RECTANGLE)
                            showBottomSheet = false
                        }
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun ShapeOption(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(CircleShape)
            .clickable { onClick() }
            .padding(16.dp)
    ) {
        Box(
            modifier = Modifier
                .clip(CircleShape)
                .background(if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant)
                .width(48.dp)
                .height(48.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}

@Preview
@Composable
fun ShapeToolButtonPreview() {
    PreviewComponent {
        ShapeToolButton(activeShape = null, onShapeSelected = {})
    }
}

