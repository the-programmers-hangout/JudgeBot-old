package me.aberrantfox.judgebot.commands

import me.aberrantfox.judgebot.arguments.LowerMemberArg
import me.aberrantfox.judgebot.dataclasses.Ban
import me.aberrantfox.judgebot.extensions.requiredPermissionLevel
import me.aberrantfox.judgebot.services.DatabaseService
import me.aberrantfox.judgebot.services.LoggingService
import me.aberrantfox.judgebot.services.Permission
import me.aberrantfox.kjdautils.api.annotation.CommandSet
import me.aberrantfox.kjdautils.api.dsl.command.commands
import me.aberrantfox.kjdautils.extensions.jda.fullName
import me.aberrantfox.kjdautils.internal.arguments.SentenceArg
import me.aberrantfox.kjdautils.internal.arguments.UserArg

@CommandSet("Ban")
fun createBanCommands(loggingService: LoggingService,
                      databaseService: DatabaseService) = commands {
    command("ban") {
        description = "Bans a member for the passed reason, deleting a given number of days messages."
        requiresGuild = true
        requiredPermissionLevel = Permission.Moderator
        execute(LowerMemberArg, SentenceArg) {
            val (target, reason) = it.args
            //TODO: record ban reason in DB
            val ban = Ban(target.id, it.guild!!.id, it.author.id, 1L, reason)
            it.guild!!.ban(target, 1, reason).queue { _ ->
                databaseService.guilds.banMember(it.guild!!, target.id, ban)
                loggingService.logUserBanned(it.guild!!, target, reason)
                it.respond("${target.fullName()} was banned.")
            }
        }
    }

    command("unban") {
        description = "Unbans a target user"
        requiresGuild = true
        requiredPermissionLevel = Permission.Moderator
        execute(UserArg) {
            val target = it.args.first
            it.guild!!.unban(it.args.first).queue { _ ->
                loggingService.logUserUnbanned(it.guild!!, target)
                it.respond("${target.fullName()} was unbanned")
            }
        }
    }

    command("getBanReason") {
        description = "Gets ban reason a target user"
        requiresGuild = true
        requiredPermissionLevel = Permission.Moderator
        execute(UserArg) {
            val target = it.args.first
            val banRecord = databaseService.guilds.getBanReason(it.guild!!, target.id)
            if (banRecord != null) {
                val moderator = it.discord.jda.getUserById(banRecord!!.moderator)

                it.respond("User **${target.asTag}** was banned by **${moderator?.asTag}** for: ${banRecord.reason}")
            } else {
                it.respond("No ban record logged for **${target.asTag}**")
            }

        }
    }

}