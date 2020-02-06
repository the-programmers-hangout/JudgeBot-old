package me.aberrantfox.judgebot.services

import me.aberrantfox.judgebot.configuration.Rule
import me.aberrantfox.kjdautils.api.annotation.Service
import me.aberrantfox.kjdautils.api.dsl.embed
import java.awt.Color

@Service
class EmbedService(val dbService: DatabaseService) {
    private val ruleColor = Color(0xff00ff)

    fun embedRule(rule: Rule) = embed {
        title = "__${rule.number}: ${rule.title}__"
        description = rule.description
        color = ruleColor
    }

    fun embedRuleDetailed(rule: Rule) = embed {
        title = "__${rule.number}: ${rule.title}__"
        description = "**Short name:** ${rule.shortName},  **Weight:** ${rule.weight}\n\n${rule.description}"
        color = ruleColor
    }

    fun embedRules(guildId: String) = embed {
        val rules = dbService.getRules(guildId).sortedBy { it.number }
        title = "Server Rules"
        color = ruleColor
        for (rule in rules) {
            field {
                name = "**__${rule.number}: ${rule.title}__**"
                value = rule.description
                inline = false
            }
        }
    }

    fun embedRulesDetailed(guildId: String) = embed {
        val rules = dbService.getRules(guildId).sortedBy { it.number }
        title = "Server Rules"
        color = ruleColor
        for (rule in rules) {
            field {
                name = "**__${rule.number}: ${rule.title}__**"
                value = "**Short name:** ${rule.shortName}, **Weight:** ${rule.weight}\n${rule.description}"
                inline = false
            }
        }
    }
}

