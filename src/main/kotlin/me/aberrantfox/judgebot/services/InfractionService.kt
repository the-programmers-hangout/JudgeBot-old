package me.aberrantfox.judgebot.services

import me.aberrantfox.judgebot.configuration.BotConfiguration
import me.aberrantfox.judgebot.services.database.dataclasses.GuildMember
import me.aberrantfox.judgebot.services.database.dataclasses.Infraction
import me.aberrantfox.judgebot.services.database.dataclasses.infractionMap
import me.aberrantfox.judgebot.utility.buildUserStatusText
import me.aberrantfox.judgebot.utility.getEmbedColor
import me.aberrantfox.kjdautils.api.annotation.Service
import me.aberrantfox.kjdautils.api.dsl.embed
import me.aberrantfox.kjdautils.extensions.jda.sendPrivateMessage
import net.dv8tion.jda.api.entities.User
import org.joda.time.DateTime
import org.joda.time.Days

@Service
class InfractionService(private val databaseService: DatabaseService, private val userService: UserService, private val ruleService: RuleService, private val config: BotConfiguration) {
    private val infractionCollection = databaseService.db.getCollection("infractionCollection")

    fun infract(target: User, userRecord: GuildMember, infraction: Infraction): GuildMember {
        userRecord.addInfraction(infraction, calculateInfractionPoints(userRecord, infraction))
        val infractionEmbed = buildInfractionEmbed(target, userRecord, infraction, config)
        target.sendPrivateMessage(infractionEmbed)
        return userService.updateUserRecord(userRecord)
    }

    // TODO: apply punishments for infraction to user, send DM etc...
    private fun calculateInfractionPoints(userRecord: GuildMember, infraction: Infraction): Int {
        val rule = ruleService.getRule(infraction.ruleBroken!!, infraction.guildId)
        val daysSinceLastInfraction = Days.daysBetween(DateTime(userRecord.lastInfraction).toLocalDate(), DateTime().toLocalDate()).days

        var dayTotal = daysSinceLastInfraction / 30
        if(dayTotal > 12) dayTotal = 12

        var points = (rule!!.weight * infractionMap[infraction.weight]!!) - dayTotal
        if (points < 0) points = 0

        return points
    }

    private fun buildInfractionEmbed(target: User, member: GuildMember, infraction: Infraction, config: BotConfiguration) =
            embed {
                val guildConfig = config.getGuildConfig(infraction.guildId)
                val memberStatus = member.getStatus(infraction.guildId, config)
                color = getEmbedColor(memberStatus)
                title = "${target.name}, you have been infracted"

                if (guildConfig?.embedThumbnail != "") {
                    thumbnail = guildConfig?.embedThumbnail
                }
                description = "Infractions are formal warnings for breaking the rules in TPH.\n" +
                        "If you think your infraction is undoubtedly unjustified, please **do not** post about it in a public channel but DM Modmail with your complaint."
                val rule = ruleService.getRule(infraction.ruleBroken!!, infraction.guildId)
                field {
                    name = "Rule Broken"
                    value = "**[${rule!!.title}](${rule.link})** \n${rule.description}"
                }

                field {
                    name = "Infraction Reason"
                    value = "${infraction.reason}"
                }

                field {
                    name = "Current Status: __${memberStatus}__"
                    value = "${buildUserStatusText(memberStatus)}"
                }
            }
}