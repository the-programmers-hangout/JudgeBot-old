package me.aberrantfox.judgebot.commands

import me.aberrantfox.judgebot.services.UserService
import me.aberrantfox.kjdautils.api.annotation.CommandSet
import me.aberrantfox.kjdautils.api.dsl.command.commands
import me.aberrantfox.kjdautils.internal.arguments.UserArg
import me.aberrantfox.kjdautils.internal.services.ConversationService


@CommandSet("Infraction")
fun createInfractionCommands(conversationService: ConversationService, userService: UserService) = commands {
    command("Infract", "Warn", "Strike") {
        description = "Use this to issue an infraction to a user"
        requiresGuild = true
        execute {
            conversationService.createConversation(it.author, it.guild!!, "Infraction-Conversation")
        }
    }

    command("history") {
        description = "Use this to view a user's record"
        requiresGuild = true
        execute(UserArg) {
            val user = userService.getOrCreateUserRecord(it.args.first, it.guild!!.id)
            it.respond(userService.getUserHistory(it.args.first, user , it.guild!!, true))
        }
    }
}
