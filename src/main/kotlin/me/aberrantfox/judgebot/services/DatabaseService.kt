package me.aberrantfox.judgebot.services

import com.mongodb.MongoClient
import com.mongodb.client.result.DeleteResult
import me.aberrantfox.judgebot.configuration.BotConfiguration
import me.aberrantfox.judgebot.configuration.Rule
import me.aberrantfox.kjdautils.api.annotation.Service
import org.litote.kmongo.*

@Service
class DatabaseService(val config: BotConfiguration) {
    private val client: MongoClient = KMongo.createClient(config.dbConfiguration.address)
    private val db = client.getDatabase("judgebot")
    private val infractionCollection = db.getCollection("infractionCollection")
    private val ruleCollection = db.getCollection<Rule>("ruleCollection")

    fun getRule(ruleNumber: Int, guildId: String) : Rule? =
            ruleCollection.findOne(Rule::number eq ruleNumber, Rule::guildId eq guildId)

    fun getRule(ruleShortName: String, guildId: String) : Rule? =
            ruleCollection.findOne(Rule::shortName eq ruleShortName, Rule::guildId eq guildId)

    fun getRules(guildId: String) : MutableList<Rule> =
            ruleCollection.find(Rule::guildId eq guildId).toMutableList()

    fun addRule(rule: Rule) = ruleCollection.insertOne(rule)

    fun deleteRule(rule: Rule) : DeleteResult =
            ruleCollection.deleteOne(Rule::_id eq rule._id)

    fun updateRule(rule: Rule) = ruleCollection.updateOne(rule)

    fun insertUserRecord() {

    }

    fun getUserHistory() {
        TODO("Implement history embed")
    }
}
