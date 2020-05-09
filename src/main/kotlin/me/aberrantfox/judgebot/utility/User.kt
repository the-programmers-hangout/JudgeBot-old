package me.aberrantfox.judgebot.utility

import me.aberrantfox.judgebot.configuration.BotConfiguration
import me.aberrantfox.judgebot.dataclasses.GuildMember
import me.aberrantfox.judgebot.dataclasses.InfractionType
import me.aberrantfox.judgebot.dataclasses.Rule
import me.aberrantfox.kjdautils.api.dsl.embed
import me.aberrantfox.kjdautils.extensions.jda.getMemberJoinString
import me.aberrantfox.kjdautils.extensions.stdlib.formatJdaDate
import me.aberrantfox.kjdautils.extensions.stdlib.toTimeString
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.MessageEmbed
import org.joda.time.format.DateTimeFormat
import java.awt.Color
import java.text.SimpleDateFormat
import java.util.*

fun buildUserStatusEmbed(target: Member,
                         member: GuildMember,
                         guild: Guild,
                         config: BotConfiguration,
                         rules: MutableList<Rule>,
                         includeModerator: Boolean): MessageEmbed {
    val userGuildDetails = member.getGuildInfo(guild.id)!!
    val infractions = userGuildDetails.infractions
    val notes = userGuildDetails.notes

    return embed {
        author {
            name = "${target.user.asTag}'s Record"
            iconUrl = target.user.effectiveAvatarUrl
        }
        color = getEmbedColor(member.getStatus(guild.id, config))

        addInlineField("Notes", "${notes.size}")
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

fun buildNotesEmbed(target: Member,
    member: GuildMember,
    guild: Guild,
    config: BotConfiguration): MessageEmbed {

    val userGuildDetails = member.getGuildInfo(guild.id)!!
    val infractions = userGuildDetails.infractions
    val notes = userGuildDetails.notes

    return embed {
        color = getEmbedColor(member.getStatus(guild.id, config))
        thumbnail = target.user.effectiveAvatarUrl

        addInlineField("Notes", "${notes.size}")
        addInlineField("Infractions", "${infractions.filter { it.guildId == guild.id }.size}")
        addInlineField("Status", "${member.getStatus(guild.id, config)}")
        addInlineField("Join date", "${guild.getMemberJoinString(target.user)}")
        addInlineField("Creation date", "${target.timeCreated.toString().formatJdaDate()}")
        addInlineField("History Invokes", "${userGuildDetails.historyCount}")

        if (userGuildDetails.notes.isEmpty()) addField("", "**User has no notes written.**")
        else {
            addField("", "**__Notes:__**")
            notes.forEachIndexed { index, note ->
                val moderator = guild.jda.retrieveUserById(note.moderator).complete().name

                field {
                    name = "ID :: ${note.id} :: Staff :: __${moderator}__"
                    value = "Noted by **${moderator}** on **${SimpleDateFormat("dd/MM/yyyy").format(Date(note.dateTime))}**"
                }
                field {
                    name = "Note"
                    value = "${note.note}"
                }
            }
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