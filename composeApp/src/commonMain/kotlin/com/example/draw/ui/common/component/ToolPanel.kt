package com.example.draw.ui.common.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.draw.data.model.brush.SolidBrush
import com.example.draw.ui.common.preview.PreviewComponent
import com.example.draw.ui.support_feature.brushConfig.color.mainComponent.ColorPickerButton
import com.example.draw.ui.support_feature.layerConfig.mainComponent.LayerListPanelButton
import draw.composeapp.generated.resources.Res
import draw.composeapp.generated.resources.solid_brush

@Composable
fun ToolPanel(
    shouldHideToolPanel: Boolean = false,
    appearFromBottom: Boolean = false,
    leftContent: @Composable RowScope.() -> Unit = {},
    centerContent: @Composable RowScope.() -> Unit = {},
    rightContent: @Composable RowScope.() -> Unit = {}
) {
    AnimatedVisibility(
        visible = !shouldHideToolPanel,
        enter = slideInVertically(
            initialOffsetY = { height ->
                if (appearFromBottom) height else -height
            }
        ) + fadeIn(),
        exit = slideOutVertically(
            targetOffsetY = { height ->
                if (appearFromBottom) height else -height
            }
        ) + fadeOut()
    ) {
        Row(
            modifier = Modifier
                .background(Color.Transparent)
                .padding(horizontal = 15.dp)
                .systemBarsPadding()
        ) {
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(15.dp)
            ) { leftContent() }

            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(15.dp)
            ) { centerContent() }

            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(15.dp)
            ) { rightContent() }
        }
    }
}



@Preview
@Composable
fun ToolPanelPreview(){
    PreviewComponent {
        ToolPanel(
            leftContent = {
                ColorPickerButton(
                    initialBrush = SolidBrush(),
                )
                ImageButton(
                    imageResource = Res.drawable.solid_brush,
                    isSelected = true,
                    onClick = {}
                )
            },
            centerContent = {
                // Add center content here for preview
            },
            rightContent = {
                LayerListPanelButton()
            }
        )
    }
}