package me.aberrantfox.judgebot.services.database.dataclasses

import org.joda.time.DateTime

enum class InfractionWeight {
    Note, Borderline, Lightly, Clearly, Harshly, Error
}

data class Infraction(
        val moderator: String,
        val reason: String,
        val dateTime: DateTime,
        val weight: InfractionWeight
)

fun convertToInfractionType(infraction: String) =
        when(infraction.toLowerCase()) {
            "note" -> InfractionWeight.Note
            "borderline" -> InfractionWeight.Borderline
            "lightly" -> InfractionWeight.Lightly
            "clearly" -> InfractionWeight.Clearly
            "harshly" -> InfractionWeight.Harshly
            else -> InfractionWeight.Error
        }