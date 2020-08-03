package me.aberrantfox.judgebot.services

import me.aberrantfox.judgebot.dataclasses.Rule
import me.jakejmattson.discordkt.api.annotations.Service
import me.jakejmattson.discordkt.api.dsl.embed.embed
import net.dv8tion.jda.api.entities.Guild
import java.awt.Color
import java.time.LocalDateTime

@Service
class EmbedService(private val databaseService: DatabaseService) {
    private val ruleColor = Color(0xff00ff)

    fun embedRule(rule: Rule) = embed {
        simpleTitle = "__${rule.number}: ${rule.title}__"
        description = rule.description
        color = ruleColor
    }

    fun embedRuleDetailed(rule: Rule) = embed {
        simpleTitle = "__${rule.number}: ${rule.title}__"
        description = "**Short name:** ${rule.shortName},  **Weight:** ${rule.weight}\n\n${rule.description}"
        color = ruleColor
    }

    fun embedRulesShort(guild: Guild) = embed {
        val rules = databaseService.rules.getRules(guild.id).sortedBy { it.number }
        simpleTitle = "**__Server Rules__**"
        color = ruleColor
        thumbnail = guild.iconUrl

        field {
            for (rule in rules) {
                value += "**[${rule.number}](${rule.link})**. ${rule.title}\n"
            }
        }
        footer {
            text = guild.name
        }
    }

    fun embedRules(guild: Guild) = embed {
        val rules = databaseService.rules.getRules(guild.id).sortedBy { it.number }
        simpleTitle = "**__Server Rules__**"
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
        val rules = databaseService.rules.getRulesSortedByNumber(guildId)
        simpleTitle = "Server Rules"
        color = ruleColor
        for (rule in rules) {
            field {
                name = "**Short name** :: ${rule.shortName.padEnd(20, ' ')} :: **Weight** :: ${rule.weight}"
                value = "**__${rule.number}: ${rule.title}__**\n${rule.description}"
                inline = false
            }
        }
    }
}

