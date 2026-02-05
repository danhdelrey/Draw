package com.example.draw.ui.support_feature.text.component

import androidx.compose.material.icons.Icons
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.draw.data.model.brush.SolidBrush
import com.example.draw.ui.common.component.ToolPanel
import com.example.draw.ui.common.component.button.ElegantButton
import com.example.draw.ui.common.preview.PreviewComponent
import com.example.draw.ui.support_feature.brushConfig.color.mainComponent.ColorPickerButton

@Composable
fun TextModeToolPanel(
    isInTextMode: Boolean = false,
){
    ToolPanel(
        appearFromBottom = true,
        shouldHideToolPanel = !isInTextMode,
    ){
        ColorPickerButton(
            initialBrush = SolidBrush()
        )

    }
}

@Preview
@Composable
fun TextModeToolPanelPreview(){
    PreviewComponent {
        TextModeToolPanel()
    }
}