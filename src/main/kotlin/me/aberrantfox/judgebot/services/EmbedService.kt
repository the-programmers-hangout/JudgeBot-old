package me.aberrantfox.judgebot.services

import me.aberrantfox.judgebot.services.database.dataclasses.Rule
import me.aberrantfox.kjdautils.api.annotation.Service
import me.aberrantfox.kjdautils.api.dsl.embed
import me.aberrantfox.kjdautils.api.dsl.menu
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
                value = "**__${rule.number}: ${rule.title}__**\n${rule.description}"
                inline = false
            }
        }
    }

    fun embedRulesDetailed(guildId: String) = embed {
        val rules = dbService.getRulesSortedByNumber(guildId)
        title = "Server Rules"
        color = ruleColor
        for (rule in rules) {
            field {
                name = "**Short name** :: ${rule.shortName.padEnd(20, ' ')} :: **Weight** :: ${rule.weight}"
                value = "**__${rule.number}: ${rule.title}__**\n${rule.description}"
                inline = false
            }
        }
    }

    fun embedRulesMenu(guildId: String) = menu {
        embed {
            val rules = dbService.getRules(guildId).sortedBy { it.number }

        }
    }
}

