package com.example.draw.ui.common.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.draw.ui.common.preview.PreviewComponent
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SliderWithLabels(
    label: String,
    valueSuffix: String = "",
    initialValue: Float,
    onValueChange: (Float) -> Unit,
    valueRange: ClosedFloatingPointRange<Float> = 1f..100f
){
    val primary = MaterialTheme.colorScheme.primary
    var currentValue by remember { mutableStateOf(initialValue) }

    Column {
        Row(
            modifier = Modifier.padding(horizontal = 5.dp)
        ) {
            Text(label, style = MaterialTheme.typography.labelLarge)
            Spacer(modifier = Modifier.weight(1f))
            Text("${currentValue.roundToInt()} $valueSuffix", style = MaterialTheme.typography.labelLarge)
        }
        Spacer(modifier = Modifier.height(5.dp))
        Slider(
            modifier = Modifier.height(12.dp),
            value = currentValue,
            onValueChange = {
                currentValue = it
                onValueChange(it)
            },
            valueRange = valueRange,
            colors = SliderDefaults.colors(
                thumbColor = Color.Transparent,
                activeTrackColor = Color.Transparent,
                inactiveTrackColor = Color.Transparent
            ),
            thumb = {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .background(
                            color = MaterialTheme.colorScheme.primary,
                            shape = CircleShape
                        )

                )
            },
            track = { sliderPositions ->
                val fraction = sliderPositions.coercedValueAsFraction

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(5.dp)
                        .clip(CircleShape)
                        .background(
                            // inactive track
                            MaterialTheme.colorScheme.outlineVariant
                        )

                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(fraction)
                            .background(primary)
                    )
                }
            }
        )
    }
}

@Preview
@Composable
fun SliderWithLabelsPreview() {
    PreviewComponent {
        SliderWithLabels(
            initialValue = 59f,
            onValueChange = {},
            label = "Size",
            valueSuffix = ""
        )
    }
}