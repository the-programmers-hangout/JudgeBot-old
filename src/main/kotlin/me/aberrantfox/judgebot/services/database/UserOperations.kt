package me.aberrantfox.judgebot.services.database

import me.aberrantfox.judgebot.dataclasses.GuildDetails
import me.aberrantfox.judgebot.dataclasses.GuildMember
import me.aberrantfox.judgebot.dataclasses.Note
import me.aberrantfox.kjdautils.api.annotation.Service
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.Member
import org.joda.time.DateTime
import org.litote.kmongo.eq
import org.litote.kmongo.findOne
import org.litote.kmongo.getCollection
import org.litote.kmongo.updateOne

@Service
class UserOperations(private val connection: ConnectionService) {
    private val userCollection = connection.db.getCollection<GuildMember>("UserCollection")

    fun getOrCreateUser(target: Member, guildId: String): GuildMember {
        var userRecord = userCollection.findOne(GuildMember::userId eq target.id)
        return if(userRecord != null) {
            userRecord.ensureGuildDetailsPresent(guildId)
            userRecord
        } else {
            val guildMember = GuildMember(target.id)
            guildMember.guilds.add(GuildDetails(guildId))
            userCollection.insertOne(guildMember)
            guildMember
        }
    }

    fun updateUser(user: GuildMember): GuildMember {
        userCollection.updateOne(GuildMember::userId eq user.userId, user)
        return user
    }

    fun addNote(guild: Guild, user: GuildMember, note: String, moderator: String): GuildMember {
        user.addNote(note, moderator, guild)
        return this.updateUser(user)
    }

    fun deleteNote(guild: Guild, user: GuildMember, noteId: Int): GuildMember {
        user.deleteNote(noteId, guild)
        return this.updateUser(user)
    }

    fun incrementUserHistory(user: GuildMember, guildId: String): GuildMember {
        user.incrementHistoryCount(guildId)
        return this.updateUser(user)
    }
}