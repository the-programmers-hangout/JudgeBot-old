package me.aberrantfox.judgebot.utility

import me.aberrantfox.judgebot.configuration.BotConfiguration
import me.aberrantfox.judgebot.dataclasses.GuildMember
import me.aberrantfox.judgebot.dataclasses.InfractionWeight
import me.aberrantfox.judgebot.dataclasses.Rule
import me.aberrantfox.kjdautils.api.dsl.embed
import me.aberrantfox.kjdautils.extensions.jda.getMemberJoinString
import me.aberrantfox.kjdautils.extensions.stdlib.formatJdaDate
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.MessageEmbed
import java.awt.Color

fun buildUserStatusEmbed(target: Member,
                         member: GuildMember,
                         guild: Guild,
                         config: BotConfiguration,
                         rules: MutableList<Rule>,
                         includeModerator: Boolean): MessageEmbed {
    val userGuildDetails = member.guilds.find { it.guildId == guild.id }!!
    val (notes, infractions) = userGuildDetails.infractions.partition { it.weight == InfractionWeight.Note }

    return embed {
        author {
            name = "${target.user.asTag}'s Record"
            iconUrl = target.user.effectiveAvatarUrl
        }
        color = getEmbedColor(member.getStatus(guild.id, config))

        addInlineField("Notes", "${notes.filter { it.guildId == guild.id }.size}")
        addInlineField("Infractions", "${infractions.filter { it.guildId == guild.id }.size}")
        addInlineField("Status", "${member.getStatus(guild.id, config)}")
        addInlineField("Join date", "${guild.getMemberJoinString(target.user)}")
        addInlineField("Creation date", "${target.timeCreated.toString().formatJdaDate()}")
        addInlineField("History Invokes", "${userGuildDetails.historyCount}")

        field {
            name = ""
            value = "**__Rule Summary:__**"
        }

        val rulesBroken = groupRulesBroken(member, guild.id)
        rules.chunked(2).forEachIndexed { index, chunkedRules ->
            chunkedRules.forEachIndexed { index, it ->
                if (index == 0) {
                    field {
                        name = "${it.number}: ${it.title}"
                        value = "Weight: **${it.weight}** :: Broken: **${rulesBroken[it.number]
                                ?: 0}** \u2800\u2800"
                        inline = true
                    }
                } else {
                    field {
                        name = "${it.number}: ${it.title}"
                        value = "Weight: **${it.weight}** :: Broken: **${rulesBroken[it.number] ?: 0}**"
                        inline = true
                    }
                }
            }
            if (index != rules.chunked(2).size - 1) addBlankField(false)
        }
    }
}

private fun groupRulesBroken(user: GuildMember, guildId: String): Map<Int?, Int> {
    return user.getGuildInfo(guildId)!!.infractions
            .filter { it.guildId == guildId }
            .groupBy { it.ruleBroken }
            .mapValues { it.value.size }
}

fun getEmbedColor(status: String): Color? {
    return when(status) {
        "Red" -> Color.RED
        "Green" -> Color.GREEN
        "Yellow" -> Color.YELLOW
        "Orange" -> Color.ORANGE
        "Clear" -> Color.LIGHT_GRAY
        else -> Color.BLACK
    }
}

fun buildUserStatusText(status: String): String {
    return when (status) {
        "Green" -> "**Green** -> Yellow -> Orange -> Red -> Ban"
        "Yellow" -> "Green -> **Yellow** -> Orange -> Red -> Ban"
        "Orange" -> "Green -> Yellow -> **Orange** -> Red -> Ban"
        "Red" -> "Green -> Yellow -> Orange -> **Red** -> Ban"
        else -> "Green -> Yellow -> Orange -> Red -> Ban"
    }
}