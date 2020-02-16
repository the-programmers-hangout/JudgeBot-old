package me.aberrantfox.judgebot.services

import com.mongodb.MongoClient
import com.mongodb.client.result.DeleteResult
import me.aberrantfox.judgebot.configuration.BotConfiguration
import me.aberrantfox.judgebot.services.database.dataclasses.GuildMember
import me.aberrantfox.judgebot.services.database.dataclasses.Rule
import me.aberrantfox.kjdautils.api.annotation.Service
import org.litote.kmongo.*
import me.aberrantfox.kjdautils.api.dsl.embed
import me.aberrantfox.kjdautils.extensions.jda.fullName
import net.dv8tion.jda.api.entities.*

@Service
open class DatabaseService(val config: BotConfiguration) {
    private val client: MongoClient = KMongo.createClient(config.dbConfiguration.address)
    private val db = client.getDatabase(config.dbConfiguration.databaseName)
    private val infractionCollection = db.getCollection("infractionCollection")
    private val ruleCollection = db.getCollection<Rule>("ruleCollection")

    init {
        ruleCollection.createIndex("{ shortName: 'text' }")
    }

    fun getRule(ruleNumber: Int, guildId: String) : Rule? =
            ruleCollection.findOne(Rule::number eq ruleNumber, Rule::guildId eq guildId)

    fun getRule(ruleShortName: String, guildId: String) : Rule? =
            ruleCollection.findOne(Rule::guildId eq guildId, Rule::shortName regex "(?i)$ruleShortName")

    fun getRules(guildId: String) : MutableList<Rule> =
            ruleCollection.find(Rule::guildId eq guildId).toMutableList()

    fun getRulesSortedByNumber(guildId: String) : List<Rule> =
            ruleCollection.find(Rule::guildId eq guildId).toMutableList().sortedBy { it.number }

    fun addRule(rule: Rule) = ruleCollection.insertOne(rule)

    fun deleteRule(rule: Rule) : DeleteResult =
            ruleCollection.deleteOne(Rule::_id eq rule._id)

    fun updateRule(rule: Rule) = ruleCollection.updateOne(rule)
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

    fun getUserHistory() {
        TODO("Implement history embed")
    }

    fun dropRuleCollection() = ruleCollection.drop()
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
