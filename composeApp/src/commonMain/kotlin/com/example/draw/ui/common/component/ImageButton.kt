package com.example.draw.ui.common.component

import androidx.compose.foundation.Image
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.draw.ui.common.preview.PreviewComponent
import draw.composeapp.generated.resources.Res
import draw.composeapp.generated.resources.solid_brush
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

@Composable
fun ImageButton(
    imageResource: DrawableResource,
    isSelected: Boolean,
    onClick: () -> Unit,
){
    Box(
        modifier = Modifier
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .border(
                width = 5.dp,
                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceContainer,
                shape = CircleShape
            )
            .clickable{
                onClick()
            }
    ) {
        Image(
            modifier = Modifier
                .padding(12.dp)
                .width(24.dp)
                .height(24.dp)

            ,
            painter = painterResource(imageResource),
            contentDescription = null
        )
    }
}

@Preview
@Composable
fun ImageButtonPreview() {
    PreviewComponent {
        ImageButton(
            imageResource = Res.drawable.solid_brush,
            isSelected = true,
            onClick = {}
        )
    }
}