package com.example.draw.ui.common.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.draw.ui.common.preview.PreviewComponent
import com.example.draw.ui.feature.colorPicker.mainComponent.ColorPickerButton
import com.example.draw.ui.feature.layerConfig.mainComponent.LayerListPanelButton
import draw.composeapp.generated.resources.Res
import draw.composeapp.generated.resources.solid_brush

@Composable
fun ToolPanel(
    leftContent: @Composable RowScope.() -> Unit = { },
    centerContent: @Composable RowScope.() -> Unit = { },
    rightContent: @Composable RowScope.() -> Unit = { }
){
    Row(
        modifier = Modifier
            .padding(horizontal = 15.dp, vertical = 10.dp)
    ) {
        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(15.dp)
        ) {
            leftContent()
        }
        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(15.dp)
        ) {
            centerContent()
        }
        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(15.dp)
        ) {
            Spacer(modifier = Modifier.weight(1f))
            rightContent()
        }
    }
}

@Preview
@Composable
fun ToolPanelPreview(){
    PreviewComponent {
        ToolPanel(
            leftContent = {
                ColorPickerButton()
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