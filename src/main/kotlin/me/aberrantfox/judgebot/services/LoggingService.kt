package me.aberrantfox.judgebot.services

import me.aberrantfox.judgebot.configuration.BotConfiguration
import me.aberrantfox.judgebot.dataclasses.Infraction
import me.aberrantfox.judgebot.dataclasses.PunishmentConfig
import me.aberrantfox.judgebot.dataclasses.PunishmentType
import me.aberrantfox.judgebot.extensions.verboseDescriptor
import me.aberrantfox.judgebot.utility.timeToString
import me.aberrantfox.kjdautils.api.annotation.Service
import me.aberrantfox.kjdautils.api.dsl.embed
import net.dv8tion.jda.api.entities.*

enum class LogType {
    Role, Punishment, Infraction
}

@Service
class LoggingService(private val config: BotConfiguration,
                     private val databaseService: DatabaseService) {

    private fun withLog(guild: Guild, logType: LogType, f: () -> MessageEmbed) =
            getLogConfig(guild.id).apply {
                when(logType) {
                    LogType.Role -> if (this.logRoles) log(guild, getLogConfig(guild.id).loggingChannel, f())
                    LogType.Punishment -> if (this.logPunishments) log(guild, getLogConfig(guild.id).loggingChannel, f())
                    LogType.Infraction -> if (this.logInfractions) log(guild, getLogConfig(guild.id).loggingChannel, f())
                }
            }

    fun logInfraction(guild: Guild, member: Member, infraction: Infraction, punishment: PunishmentConfig) = withLog(guild, LogType.Infraction) {
        val moderator = guild.jda.retrieveUserById(infraction.moderator).complete().asMention
        val rule = databaseService.rules.getRule(infraction.ruleBroken!!, guild.id)

        embed {
            title = "User Infracted"
            color = infoColor
            thumbnail = member.user.effectiveAvatarUrl
            addField("User", member.user.verboseDescriptor())
            addField("", "**Infraction Details:**")
            addInlineField("Moderator", moderator)
            addInlineField("Weight", infraction.weight.toString())
            addField("Rule Broken", "**#${infraction.ruleBroken}** ([${rule?.title}](${rule?.link}))")
            addField("Reason", infraction.reason)
            addField("Punishment", "**${punishment.punishment}** for **${timeToString(punishment.time!!)}**")
        }
    }

    fun logRoleApplied(guild: Guild, member: Member, role: Role, time: Long) = withLog(guild, LogType.Role) {
        embed {
            title = "Role Added"
            color = successColor
            thumbnail = member.user.effectiveAvatarUrl
            addField("User", member.user.verboseDescriptor())
            addField("Role", role.verboseDescriptor())
            addField("Time", timeToString(time))
        }
    }

    fun logRoleRemoved(guild: Guild, member: Member, role: Role) = withLog(guild, LogType.Role) {
        embed {
            title = "Role Removed"
            color = failureColor
            thumbnail = member.user.effectiveAvatarUrl
            addField("User", member.user.verboseDescriptor())
            addField("Role", role.verboseDescriptor())
        }
    }

    fun logPunishment(guild: Guild, member: Member, type: PunishmentType) = withLog(guild, LogType.Punishment) {
        embed {
            title = "Punishment Applied"
            color = successColor
            thumbnail = member.user.effectiveAvatarUrl
            addField("User", member.user.verboseDescriptor())
            addField("Punishment", type.toString())
        }
    }

    fun logPunishmentCancelled(guild: Guild, member: Member, type: PunishmentType) = withLog(guild, LogType.Punishment) {
        embed {
            title = "Punishment Cancelled"
            color = failureColor
            thumbnail = member.user.effectiveAvatarUrl
            addField("User", member.user.verboseDescriptor())
            addField("Punishment", type.toString())
        }
    }

    fun logUserBanned(guild: Guild, member: Member, reason: String) = withLog(guild, LogType.Punishment) {
        embed {
            title = "User Banned"
            color = infoColor
            thumbnail = member.user.effectiveAvatarUrl
            addField("User", member.user.verboseDescriptor())
            addField("Reason", reason)
        }
    }

    fun logUserUnbanned(guild: Guild, member: User) = withLog(guild, LogType.Punishment) {
        embed {
            title = "User Unbanned"
            color = infoColor
            thumbnail = member.effectiveAvatarUrl
            addField("User", member.verboseDescriptor())
        }
    }

    private fun getLogConfig(guildId: String) = config.getGuildConfig(guildId)!!.loggingConfiguration
    private fun log(guild: Guild, logChannelId: String, message: MessageEmbed) = logChannelId.takeIf { it.isNotEmpty() }?.idToTextChannel(guild)
            ?.sendMessage(message)?.queue()

    private fun String.idToTextChannel(guild: Guild) = guild.jda.getTextChannelById(this)
}