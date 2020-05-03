package me.aberrantfox.judgebot.services.database

import com.mongodb.client.FindIterable
import me.aberrantfox.judgebot.dataclasses.Punishment
import me.aberrantfox.judgebot.dataclasses.PunishmentType
import me.aberrantfox.kjdautils.api.annotation.Service
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.Member
import org.litote.kmongo.*

@Service
class PunishmentOperations(private val connection: ConnectionService) {
    private val punishmentCollection = connection.db.getCollection<Punishment>("PunishmentCollection")

    fun addPunishment(punishment: Punishment) = punishmentCollection.insertOne(punishment)

    fun findByType(member: Member, guild: Guild, type: PunishmentType): Punishment? =
            punishmentCollection.findOne(and(
                    Punishment::userId eq member.id,
                    Punishment::guildId eq guild.id,
                    Punishment::type eq type))

    fun findByGuild(guild: Guild): FindIterable<Punishment> =
            punishmentCollection.find(Punishment::guildId eq guild.id)

    fun removePunishment(member: Member, guild: Guild, type: PunishmentType): Punishment? =
            punishmentCollection.findOneAndDelete(and(
                    Punishment::userId eq member.id,
                    Punishment::guildId eq guild.id,
                    Punishment::type eq type))
}