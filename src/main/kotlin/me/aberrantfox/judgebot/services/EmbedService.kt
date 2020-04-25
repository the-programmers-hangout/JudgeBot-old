package me.aberrantfox.judgebot.services

import me.aberrantfox.judgebot.configuration.BotConfiguration
import me.aberrantfox.judgebot.services.database.dataclasses.Rule
import me.aberrantfox.kjdautils.api.annotation.Service
import me.aberrantfox.kjdautils.api.dsl.embed
import me.aberrantfox.kjdautils.api.dsl.menu
import net.dv8tion.jda.api.entities.Guild
import org.joda.time.DateTime
import java.awt.Color
import java.time.LocalDateTime

@Service
class EmbedService(private val ruleService: RuleService, private val config: BotConfiguration) {
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

    fun embedRulesShort(guild: Guild) = embed {
        val rules = ruleService.getRules(guild.id).sortedBy { it.number }
        title = "**__Server Rules__**"
        color = ruleColor
        thumbnail = guild.iconUrl

        field {
            for (rule in rules) {
                value += "**[${rule.number}](${rule.link})**. ${rule.title}\n"
            }
        }
        footer {
            text = guild.name
            timeStamp = LocalDateTime.now()
        }
    }

    fun embedRules(guild: Guild) = embed {
        val rules = ruleService.getRules(guild.id).sortedBy { it.number }
        title = "**__Server Rules__**"
        color = ruleColor
        thumbnail = guild.iconUrl

        for (rule in rules) {
            field {
                value = "**__${rule.number}: ${rule.title}__**\n${rule.description}"
                inline = false
            }
        }
    }

    fun embedRulesDetailed(guildId: String) = embed {
        val rules = ruleService.getRulesSortedByNumber(guildId)
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
            val rules = ruleService.getRules(guildId).sortedBy { it.number }

        }
    }
}

