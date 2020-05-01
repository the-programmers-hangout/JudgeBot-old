package me.aberrantfox.judgebot.commands

import me.aberrantfox.judgebot.conversations.InfractionConversation
import me.aberrantfox.judgebot.services.BlindfoldService
import me.aberrantfox.judgebot.services.MuteService
import me.aberrantfox.judgebot.extensions.requiredPermissionLevel
import me.aberrantfox.judgebot.services.Permission
import me.aberrantfox.judgebot.services.UserService
import me.aberrantfox.kjdautils.api.annotation.CommandSet
import me.aberrantfox.kjdautils.api.dsl.command.commands
import me.aberrantfox.kjdautils.extensions.jda.fullName
import me.aberrantfox.kjdautils.internal.arguments.*
import me.aberrantfox.kjdautils.internal.services.ConversationService
import kotlin.math.roundToLong


@CommandSet("Infraction")
fun createInfractionCommands(conversationService: ConversationService, userService: UserService, muteService: MuteService,
                             blindfoldService: BlindfoldService) = commands {
    command("Infract", "Warn", "Strike") {
        description = "Use this to issue an infraction to a user"
        requiresGuild = true
        requiredPermissionLevel = Permission.Staff
        execute {
            conversationService.startConversation<InfractionConversation>(it.author, it.guild!!)
        }
    }

    command("history") {
        description = "Use this to view a user's record"
        requiresGuild = true
        requiredPermissionLevel = Permission.Staff
        execute(UserArg) {
            val user = userService.getOrCreateUserRecord(it.args.first, it.guild!!.id)
            it.respond(userService.getUserHistory(it.args.first, user , it.guild!!, true))
        }
    }

    command("mute") {
        description = "Use this to mute a member"
        requiresGuild = true
        execute(MemberArg, TimeStringArg, SentenceArg) {
            val time = (it.args.second as Double).roundToLong() * 1000
            muteService.mute(it.args.first, time, it.args.third)
            it.respond("User ${it.args.first.fullName()} has been muted")
        }
    }

    command("unmute") {
        description = "Use this to mute a member"
        requiresGuild = true
        execute(MemberArg) {
            muteService.unmute(it.args.first)
            it.respond("User ${it.args.first.fullName()} has been unmuted")
        }
    }

    command("blindfold") {
        description = "Use this to mute a member"
        requiresGuild = true
        execute(MemberArg, TimeStringArg, SentenceArg) {
            val time = (it.args.second as Double).roundToLong() * 1000
            blindfoldService.blindfold(it.args.first, time, it.args.third)
            it.respond("User ${it.args.first.fullName()} has been blindfolded")
        }
    }
    command("unblindfold") {
        description = "Use this to mute a member"
        requiresGuild = true
        execute(MemberArg) {
            blindfoldService.unblindfold(it.args.first)
            it.respond("User ${it.args.first.fullName()} has been unblindfolded")
        }
    }
}
