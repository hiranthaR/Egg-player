package xyz.hirantha.jajoplayer.internal

import org.threeten.bp.Duration

fun Duration.toMMSS(): String {
    val formattedMinutes = String.format("%02d", this.toMinutes())
    val formattedSeconds = String.format("%02d", this.seconds % 60)
    return "${formattedMinutes}:${formattedSeconds}"
}