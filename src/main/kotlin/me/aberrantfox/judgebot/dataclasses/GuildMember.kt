package me.aberrantfox.judgebot.dataclasses

import me.aberrantfox.judgebot.configuration.BotConfiguration
import net.dv8tion.jda.api.entities.Guild
import java.util.*

data class GuildDetails (
        val guildId: String,
        val infractions: MutableList<Infraction> = mutableListOf<Infraction>(),
        val notes: MutableList<Note> = mutableListOf<Note>(),
        var historyCount: Int = 0,
        var points: Int = 0,
        var lastInfraction: Long = 0
)

data class GuildMember (
        val userId: String,
        val guilds: MutableList<GuildDetails> = mutableListOf<GuildDetails>()
) {

    fun incrementHistoryCount(guildId: String) {
        this.getGuildInfo(guildId)!!.historyCount += 1
    }

    fun addInfraction(infraction: Infraction, points: Int) = with(this.getGuildInfo(infraction.guildId)) {
        this?.infractions?.add(infraction)
        this?.lastInfraction = infraction.dateTime
        this!!.points += points
    }

    fun addNote(note: String, moderator: String, guild: Guild) = with(this.getGuildInfo(guild.id)) {
       this?.notes?.add(Note(note, moderator, Date().time, this.notes.size + 1))
    }

    fun getStatus(guildId: String, config: BotConfiguration): String {
        val guildDetails = this.getGuildInfo(guildId)
        var status: String = ""

        for (entry in config.getGuildConfig(guildId)!!.security.pointsToStatusMap.toSortedMap()) {
            if (guildDetails!!.points >= entry.key) status = entry.value
        }
        return status
    }

    fun getGuildInfo(guildId: String): GuildDetails? {
        return this.guilds.firstOrNull { it.guildId == guildId }
    }

    fun ensureGuildDetailsPresent(guildId: String) {
        if (this.guilds.any{ it.guildId == guildId }) return
        this.guilds.add(GuildDetails(guildId))
    }
}
