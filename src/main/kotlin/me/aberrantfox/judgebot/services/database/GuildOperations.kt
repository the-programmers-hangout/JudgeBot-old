package me.aberrantfox.judgebot.services.database

import me.aberrantfox.judgebot.dataclasses.Ban
import me.aberrantfox.judgebot.dataclasses.GuildInfo
import me.jakejmattson.discordkt.api.annotations.Service
import net.dv8tion.jda.api.entities.Guild
import org.litote.kmongo.eq
import org.litote.kmongo.findOne
import org.litote.kmongo.getCollection
import org.litote.kmongo.updateOne

@Service
class GuildOperations(private val connection: ConnectionService) {
    private val guildCollection = connection.db.getCollection<GuildInfo>("GuildCollection")

    fun setupGuild(guild: Guild): GuildInfo {
        val guildInfo = GuildInfo(guild.id)
        this.guildCollection.insertOne(guildInfo)
        return guildInfo
    }

    fun banMember(guild: Guild, userId: String, ban: Ban): GuildInfo = with(getGuild(guild.id)) {
        this?.addBannedUser(ban)
        return updateGuild(guild.id, this!!)
    }

    fun getBanReason(guild: Guild, userId: String) = with(getGuild(guild.id)){
        return@with this?.getBanRecord(userId)
    }

    fun updateGuild(guildId: String, updatedGuild: GuildInfo): GuildInfo {
        guildCollection.updateOne(GuildInfo::guildId eq guildId, updatedGuild)
        return updatedGuild
    }

    fun getGuild(guildId: String): GuildInfo? {
        return guildCollection.findOne(GuildInfo::guildId eq guildId)
    }
}