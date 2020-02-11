package me.aberrantfox.judgebot.services.database.dataclasses

import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat

enum class InfractionWeight {
    Note, Borderline, Lightly, Clearly, Harshly, Error
}

data class Infraction(
        val moderator: String,
        val reason: String,
        val weight: InfractionWeight,
        val guildId: String,
        val infractionNote: String? = null,
        val ruleBroken: Int? = null,
        val dateTime: String = DateTime().toString(DateTimeFormat.forPattern("dd/MM/yyyy"))
        )

fun convertToInfractionType(infraction: String) =
        InfractionWeight.values().firstOrNull {it.name.toLowerCase() == infraction.toLowerCase()}
