package me.aberrantfox.judgebot.services.database.dataclasses

import java.util.Date;

enum class InfractionWeight {
    Note, Borderline, Lightly, Clearly, Harshly
}

val infractionMap : HashMap<InfractionWeight, Int> = hashMapOf(
        InfractionWeight.Note to 0,
        InfractionWeight.Borderline to 1,
        InfractionWeight.Lightly to 2,
        InfractionWeight.Clearly to 3,
        InfractionWeight.Harshly to 4
)

data class Infraction(
        val moderator: String,
        val reason: String,
        val weight: InfractionWeight,
        val guildId: String,
        val infractionNote: String? = null,
        val ruleBroken: Int? = null,
        val dateTime: Long = Date().time
        )

fun convertToInfractionType(infraction: String) =
        InfractionWeight.values().firstOrNull {it.name.toLowerCase() == infraction.toLowerCase()}
