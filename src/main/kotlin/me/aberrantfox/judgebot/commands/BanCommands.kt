package me.aberrantfox.judgebot.commands

import me.aberrantfox.judgebot.arguments.LowerMemberArg
import me.aberrantfox.judgebot.extensions.requiredPermissionLevel
import me.aberrantfox.judgebot.services.Permission
import me.aberrantfox.kjdautils.api.annotation.CommandSet
import me.aberrantfox.kjdautils.api.dsl.command.commands
import me.aberrantfox.kjdautils.extensions.jda.fullName
import me.aberrantfox.kjdautils.internal.arguments.SentenceArg
import me.aberrantfox.kjdautils.internal.arguments.UserArg

@CommandSet("Ban")
fun createBanCommands() = commands {
    command("ban") {
        description = "Bans a member for the passed reason, deleting a given number of days messages."
        requiresGuild = true
        requiredPermissionLevel = Permission.Moderator
        execute(LowerMemberArg, SentenceArg) {
            val (target, reason) = it.args
            //TODO: record ban reason in DB
            it.guild!!.ban(target, 1, reason).queue { _ ->
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
                it.respond("${target.fullName()} was unbanned")
            }
        }
    }
}