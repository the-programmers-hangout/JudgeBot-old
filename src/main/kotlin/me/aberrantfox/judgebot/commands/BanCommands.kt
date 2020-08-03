package me.aberrantfox.judgebot.commands

import me.aberrantfox.judgebot.arguments.LowerMemberArg
import me.aberrantfox.judgebot.extensions.requiredPermissionLevel
import me.aberrantfox.judgebot.services.LoggingService
import me.aberrantfox.judgebot.services.Permission
import me.jakejmattson.discordkt.api.annotations.CommandSet
import me.jakejmattson.discordkt.api.arguments.EveryArg
import me.jakejmattson.discordkt.api.arguments.UserArg
import me.jakejmattson.discordkt.api.dsl.command.commands
import me.jakejmattson.discordkt.api.extensions.jda.fullName


@CommandSet("Ban")
fun createBanCommands(loggingService: LoggingService) = commands {
    command("ban") {
        description = "Bans a member for the passed reason, deleting a given number of days messages."
        requiresGuild = true
        requiredPermissionLevel = Permission.Moderator
        execute(LowerMemberArg, EveryArg) {
            val (target, reason) = it.args
            //TODO: record ban reason in DB
            it.guild!!.ban(target, 1, reason).queue { _ ->
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
}