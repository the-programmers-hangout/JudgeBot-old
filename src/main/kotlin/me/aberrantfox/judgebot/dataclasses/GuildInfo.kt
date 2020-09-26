package me.aberrantfox.judgebot.dataclasses

data class GuildInfo (
        val guildId: String,
        val bans: MutableList<Ban> = mutableListOf()
) {
    fun addBannedUser(banInfo: Ban) {
        this.bans.add(banInfo)
    }

    fun getBanRecord(userId: String): Ban? {
        return this.bans.firstOrNull { it.userId == userId }
    }

    fun setBanReason(userId: String, reason: String) {
        this.bans.firstOrNull { it.userId == userId }?.reason = reason
    }

//    fun getGuildInfo(guildId: String): GuildDetails? {
//        return this.guilds.firstOrNull { it.guildId == guildId }
//    }
//
//    fun ensureGuildDetailsPresent(guildId: String) {
//        if (this.guilds.any{ it.guildId == guildId }) return
//        this.guilds.add(GuildDetails(guildId))
//    }
}