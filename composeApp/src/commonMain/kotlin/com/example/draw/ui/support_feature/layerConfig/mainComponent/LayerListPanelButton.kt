package com.example.draw.ui.support_feature.layerConfig.mainComponent

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Layers
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.draw.ui.common.component.CustomIconButton
import com.example.draw.ui.common.preview.PreviewComponent

@Composable
fun LayerListPanelButton(
    onClick: () -> Unit = {}
) {

    CustomIconButton(
        icon = Icons.Default.Layers,
        enabled = true,
        onClick = {
            onClick()
        }
    )


}

@Preview
@Composable
fun LayerListPanelButtonPreview() {
    PreviewComponent {
        LayerListPanelButton(

        )
    }
}