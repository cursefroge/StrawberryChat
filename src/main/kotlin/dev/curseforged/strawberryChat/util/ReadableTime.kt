package dev.curseforged.strawberryChat.util

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

fun convertToReadableDate(unix: Long): String {
    val instant = Instant.ofEpochMilli(unix)
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        .withZone(ZoneId.of("America/New_York"))
    return formatter.format(instant)
}

fun convertToReadableTime(unix: Long): String {
    val instant = Instant.ofEpochMilli(unix)
    val formatter = DateTimeFormatter.ofPattern("HH:mm:ss")
        .withZone(ZoneId.of("America/New_York"))
    return formatter.format(instant)
}
