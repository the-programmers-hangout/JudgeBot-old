package me.aberrantfox.judgebot.utility

import java.awt.Color

fun getEmbedColor(status: String): Color? {
    return when(status) {
        "Red" -> Color.RED
        "Green" -> Color.GREEN
        "Yellow" -> Color.YELLOW
        "Orange" -> Color.ORANGE
        "Clear" -> Color.LIGHT_GRAY
        else -> Color.BLACK
    }
}

fun buildUserStatusText(status: String): String {
    return when (status) {
        "Green" -> "**Green** -> Yellow -> Orange -> Red -> Ban"
        "Yellow" -> "Green -> **Yellow** -> Orange -> Red -> Ban"
        "Orange" -> "Green -> Yellow -> **Orange** -> Red -> Ban"
        "Red" -> "Green -> Yellow -> Orange -> **Red** -> Ban"
        else -> "Green -> Yellow -> Orange -> Red -> Ban"
    }
}