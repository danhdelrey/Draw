package com.example.draw.ui.common.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.draw.ui.common.preview.PreviewComponent

@Composable
fun WavyLinePreview(color: Color, strokeWidth: Dp, opacity: Float) {
    Canvas(modifier = Modifier
        .fillMaxWidth(0.8f) // Chiều rộng nét vẽ chiếm 80% màn hình
        .height(50.dp)
    ) {
        val width = size.width
        val height = size.height

        // Tạo đường cong Bezier để giả lập nét vẽ tự nhiên
        val path = Path().apply {
            moveTo(0f, height * 0.7f)
            // Điểm điều khiển 1 (lên)
            quadraticTo(
                width * 0.25f, height * 0.1f,
                width * 0.5f, height * 0.6f
            )
            // Điểm điều khiển 2 (xuống rồi lên nhẹ)
            quadraticTo(
                width * 0.75f, height * 0.9f,
                width, height * 0.2f
            )
        }

        drawPath(
            path = path,
            color = color,
            style = Stroke(
                width = strokeWidth.toPx(), // Độ dày nét vẽ
                cap = StrokeCap.Round, // Đầu bút tròn
                join = StrokeJoin.Round
            ),
            alpha = opacity
        )
    }
}

@Preview
@Composable
fun WavyLineComponentPreview(){
    PreviewComponent {
        WavyLinePreview(color = Color(0xFF0000CC), strokeWidth = 30.dp, opacity = 1f)
    }
}