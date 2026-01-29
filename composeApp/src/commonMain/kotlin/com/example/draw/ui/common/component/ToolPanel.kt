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
import androidx.compose.foundation.layout.fillMaxWidth
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
import com.example.draw.ui.support_feature.brushConfig.brush.mainComponent.BrushConfigButton
import com.example.draw.ui.support_feature.brushConfig.color.mainComponent.ColorPickerButton
import com.example.draw.ui.support_feature.layerConfig.mainComponent.LayerListPanelButton
import com.example.draw.ui.support_feature.undoRedo.mainComponent.RedoButton
import com.example.draw.ui.support_feature.undoRedo.mainComponent.UndoButton
import draw.composeapp.generated.resources.Res
import draw.composeapp.generated.resources.solid_brush

@Composable
fun ToolPanel(
    shouldHideToolPanel: Boolean = false,
    appearFromBottom: Boolean = false,
    content: @Composable RowScope.() -> Unit = {},
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
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceContainerLowest)
                ,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            content()
        }
    }
}



@Preview
@Composable
fun ToolPanelPreview(){
    PreviewComponent {
        ToolPanel(
           content = {
               ColorPickerButton(
                   initialBrush = SolidBrush(
                       colorArgb = 0xFFFF0000,
                       size = 10f
                   )
               )
               UndoButton {  }
                BrushConfigButton(
                     currentBrush = SolidBrush(
                          colorArgb = 0xFFFF0000,
                          size = 10f
                     )
                )
               RedoButton()

                LayerListPanelButton()
           }
        )
    }
}