package me.aberrantfox.judgebot.dataclasses

data class GuildInfo (
        val guildId: String,
        val bans: MutableList<Punishment> = mutableListOf()
) {
    fun addBannedUser(banInfo: Punishment) {
        this.bans.add(banInfo)
    }

    fun getBanReason(userId: String): String {
        return this.bans.firstOrNull { it.userId == userId }?.reason ?: "No ban reason found."
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