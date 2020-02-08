package me.aberrantfox.judgebot.services

import com.mongodb.MongoClient
import me.aberrantfox.judgebot.configuration.BotConfiguration
import me.aberrantfox.judgebot.services.database.dataclasses.GuildMember
import me.aberrantfox.kjdautils.api.annotation.Service
import me.aberrantfox.kjdautils.api.dsl.embed
import me.aberrantfox.kjdautils.extensions.jda.fullName
import net.dv8tion.jda.api.entities.*
import org.litote.kmongo.*

@Service
class DatabaseService(val config: BotConfiguration) {
    private val client: MongoClient = KMongo.createClient(config.dbConfiguration.address)
    private val db = client.getDatabase("judgebot")
    private val infractionCollection = db.getCollection("infractionCollection")
    private val userCollection = db.getCollection<GuildMember>("userCollection")

    fun getOrCreateUserRecord(target: User): GuildMember {
        var userRecord = userCollection.findOne(GuildMember::userId eq target.id)
        return if(userRecord != null)
            userRecord else
            {
                userCollection.insertOne(GuildMember(target.id))
                return GuildMember(target.id)
            }
    }

    fun getUserHistory(target: User, userRecord: GuildMember): MessageEmbed {
        this.incrementUserHistory(userRecord)
        return buildHistoryEmbed(target, userRecord, true)
    }

    private fun incrementUserHistory(user: GuildMember): GuildMember {
        user.incrementHistoryCount()
        this.userCollection.updateOne(GuildMember::userId eq user.userId, user)
        return user
    }

    private fun buildHistoryEmbed(target: User, member: GuildMember, includeModerator: Boolean)  =
        embed {
            title = "${target.fullName()}'s Record"
            thumbnail = target.effectiveAvatarUrl

            field {
                value = "__**Summary**__"
                inline = false
            }

            field {
                name = "Information"

                if(includeModerator){
                    value +="\nHistory has been invoked **${member.historyCount}** times."
                }
            }

            //TODO: Build out embed with more details
        }
    }