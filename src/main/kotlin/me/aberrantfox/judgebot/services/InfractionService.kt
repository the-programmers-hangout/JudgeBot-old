package me.aberrantfox.judgebot.services

import me.aberrantfox.judgebot.configuration.BotConfiguration
import me.aberrantfox.judgebot.dataclasses.*
import me.aberrantfox.judgebot.utility.buildUserStatusText
import me.aberrantfox.judgebot.utility.getEmbedColor
import me.aberrantfox.kjdautils.api.annotation.Service
import me.aberrantfox.kjdautils.api.dsl.embed
import me.aberrantfox.kjdautils.extensions.jda.sendPrivateMessage
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.Member
import org.joda.time.DateTime
import org.joda.time.Days

@Service
class InfractionService(private val config: BotConfiguration,
                        private val badPfpService: BadPfpService,
                        private val databaseService: DatabaseService,
                        private val roleService: RoleService) {

    fun infract(target: Member, guild: Guild, userRecord: GuildMember, infraction: Infraction): GuildMember {
        userRecord.addInfraction(infraction, calculateInfractionPoints(userRecord, infraction))
        val infractionEmbed = buildInfractionEmbed(target, userRecord, guild, infraction, config)
        val punishment = calculatePunishment(target, userRecord, guild)

        applyPunishment(target, userRecord, punishment!!, guild)
        target.user.sendPrivateMessage(infractionEmbed)
        return databaseService.users.updateUser(userRecord)
    }

    private fun calculateInfractionPoints(userRecord: GuildMember, infraction: Infraction): Int {
        val rule = databaseService.rules.getRule(infraction.ruleBroken!!, infraction.guildId)
        val lastInfractionOffset = calculatePeriodOffset(userRecord, infraction.guildId)

        var points = (rule!!.weight * infractionMap[infraction.weight]!!) - lastInfractionOffset
        if (points < 0) points = 0

        return points
    }

    private fun calculatePeriodOffset(userRecord: GuildMember, guildId: String): Int {
        if(userRecord.getGuildInfo(guildId)!!.infractions.size == 0) return 0

        val daysSinceLastInfraction = Days.daysBetween(DateTime(userRecord.getGuildInfo(guildId)?.lastInfraction), DateTime()).days
        var dayTotal = daysSinceLastInfraction / 30
        if(dayTotal > 12) dayTotal = 12
        return dayTotal
    }

    private fun applyPunishment(target: Member, guildMember: GuildMember, punishmentConfig: PunishmentConfig, guild: Guild) {
        when (punishmentConfig.punishment) {
            PunishmentType.BadPfp -> badPfpService.applyBadPfp(target, punishmentConfig, guild)

            PunishmentType.Mute, PunishmentType.Blindfold ->
                roleService.applyRole(target, punishmentConfig.time!!, "Infraction Punishment", punishmentConfig.punishment)

            //TODO: Save bans with timeframes to DB, update to bettter reason
            PunishmentType.TemporaryBan, PunishmentType.AppealableBan, PunishmentType.PermanentBan ->
                guild.ban(target.user, 1, "Infraction Punishment").queue()
        }
    }

    fun calculatePunishment(target: Member, userRecord: GuildMember, guild: Guild): PunishmentConfig? {
        val guildDetails = config.getGuildConfig(guild.id)!!

        return guildDetails.security.pointsToPunishmentMap.toSortedMap().filterKeys {
            it <= userRecord.getGuildInfo(guild.id)!!.points
        }.maxBy { it.key }!!.value
    }

    private fun buildInfractionEmbed(target: Member, member: GuildMember, guild:Guild, infraction: Infraction, config: BotConfiguration) =
            embed {
                val memberStatus = member.getStatus(infraction.guildId, config)
                color = getEmbedColor(memberStatus)
                title = "${target.user.name}, you have been infracted"
                thumbnail = guild.iconUrl
                description = "Infractions are formal warnings for breaking the rules in TPH.\n" +
                        "If you think your infraction is undoubtedly unjustified, please **do not** post about it in a public channel but DM Modmail with your complaint."
                val rule = databaseService.rules.getRule(infraction.ruleBroken!!, infraction.guildId)
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