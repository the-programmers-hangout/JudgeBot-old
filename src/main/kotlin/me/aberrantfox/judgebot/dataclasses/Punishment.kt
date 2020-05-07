package me.aberrantfox.judgebot.dataclasses

data class PunishmentConfig(val punishment: PunishmentType, val time: Long? = null)

enum class PunishmentType {
    Warn, Mute, BadPfp, Blindfold, TemporaryBan, AppealableBan, PermanentBan
}

data class Punishment(val userId: String, val guildId: String, val type: PunishmentType, val clearTime: Long, val reason: String)