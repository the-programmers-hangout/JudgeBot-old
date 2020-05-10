package me.aberrantfox.judgebot.dataclasses

import java.util.Date;

enum class InfractionType {
    Note, Borderline, Light, Clear, Harsh
}

val infractionMap : HashMap<InfractionType, Int> = hashMapOf(
        InfractionType.Borderline to 0,
        InfractionType.Light to 1,
        InfractionType.Clear to 2,
        InfractionType.Harsh to 3
)

data class Infraction(
        val moderator: String,
        val reason: String,
        val weight: InfractionType,
        val guildId: String,
        val infractionNote: String? = null,
        val ruleBroken: Int? = null,
        val dateTime: Long = Date().time
        )

fun convertToInfractionType(infraction: String) =
        InfractionType.values().firstOrNull {it.name.toLowerCase() == infraction.toLowerCase()}
