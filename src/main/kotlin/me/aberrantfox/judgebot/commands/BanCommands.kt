package me.aberrantfox.judgebot.commands

import me.aberrantfox.judgebot.arguments.LowerMemberArg
import me.aberrantfox.judgebot.extensions.requiredPermissionLevel
import me.aberrantfox.judgebot.services.*
import me.jakejmattson.discordkt.api.annotations.CommandSet
import me.jakejmattson.discordkt.api.arguments.EveryArg
import me.jakejmattson.discordkt.api.arguments.UserArg
import me.jakejmattson.discordkt.api.dsl.command.commands
import me.jakejmattson.discordkt.api.extensions.jda.fullName
import me.jakejmattson.discordkt.api.extensions.jda.toMember
import org.joda.time.DateTime


@CommandSet("Ban")
fun createBanCommands(loggingService: LoggingService, db: DatabaseService) = commands {
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

    command("testjoinleave", "tjl") {
        execute(UserArg) {
            val target = it.args.first
            val userRecord = db.users.getOrCreateUser(it.args.first.toMember(it.guild!!)!!, it.guild!!.id)
            val leaveTime = DateTime.now().millis
            db.users.insertGuildLeave(userRecord, it.guild!!, it.author.toMember(it.guild!!)!!.timeJoined.toEpochSecond() * 1000 , leaveTime)

        }
    }
}