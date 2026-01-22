package com.example.draw.ui.support_feature.colorPicker.mockData

import androidx.compose.ui.graphics.Color


enum class MockColorPalette(val color: Color) {
    LIME(Color(0xFF66FF00)),
    RED(Color(0xFFFF0000)),
    BLUE(Color(0xFF0000CC)),
    OLIVE(Color(0xFF808000)),
    DEEPSKYBLUE(Color(0xFF00BFFF)),
    DARKRED(Color(0xFF550000)),
    ORANGE(Color(0xFFFFA500)),
    DARKGREEN(Color(0xFF006400)),
    DARKPURPLE(Color(0xFF5D2450)),
    DEEPPINK(Color(0xFFFF1493)),
    CRIMSON(Color(0xFFDC143C)),
    SEAGREEN(Color(0xFF2E8B57)),
    GOLD(Color(0xFFFFD700)),
    MIDNIGHTBLUE(Color(0xFF191970)),
    BROWN(Color(0xFFA52A2A)),
    DARKBLUE(Color(0xFF003366)),
    ROSYBROWN(Color(0xFFC08081)),
    DARKSLATEGRAY(Color(0xFF2F4F4F));

    companion object {
        fun toList(): List<Color> {
            return values().map { it.color }
        }
    }

}