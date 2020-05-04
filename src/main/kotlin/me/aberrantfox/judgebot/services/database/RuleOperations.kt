package me.aberrantfox.judgebot.services.database

import com.mongodb.client.result.DeleteResult
import me.aberrantfox.judgebot.dataclasses.Rule
import me.aberrantfox.kjdautils.api.annotation.Service
import org.litote.kmongo.*

@Service
class RuleOperations(private val connection: ConnectionService) {
    private val ruleCollection = connection.db.getCollection<Rule>("RuleCollection")

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

    fun dropRuleCollection() = ruleCollection.drop()

}