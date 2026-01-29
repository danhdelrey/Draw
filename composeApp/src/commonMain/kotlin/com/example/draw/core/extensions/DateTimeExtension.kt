package com.example.draw.core.extensions

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.format.char
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Instant

fun Long.toDateTimeString(
    timeZone: TimeZone = TimeZone.currentSystemDefault()
): String {
    val instant = Instant.fromEpochMilliseconds(this)
    val localDateTime = instant.toLocalDateTime(timeZone)

    val formatter = LocalDateTime.Format {
        hour()
        char(':')
        minute()
        char('\n') // Thêm ký tự xuống dòng
        day()
        char('/')
        monthNumber()
        char('/')
        year()
    }

    return localDateTime.format(formatter)
}