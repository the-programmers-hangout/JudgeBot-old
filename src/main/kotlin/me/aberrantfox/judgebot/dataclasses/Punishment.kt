package me.aberrantfox.judgebot.dataclasses

data class PunishmentConfig(val punishment: PunishmentType, val time: Int? = null)

enum class PunishmentType {
    Warn, Mute, Blindfold, AppealableBan, PermanentBan
}

data class Punishment(val userId: String, val guildId: String, val type: PunishmentType, val clearTime: Long, val reason: String)