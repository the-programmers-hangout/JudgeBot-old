package me.aberrantfox.judgebot.commands

import me.aberrantfox.kjdautils.api.annotation.CommandSet
import me.aberrantfox.kjdautils.api.dsl.command.commands
import me.aberrantfox.kjdautils.internal.services.ConversationService


@CommandSet("Infraction")
fun createInfractionCommands(conversationService: ConversationService) = commands {
    command("Infract", "Warn", "Strike") {
        description = "Use this to issue an infraction to a user"
        requiresGuild = true
        execute {
            conversationService.createConversation(it.author, it.guild!!, "Infraction-Conversation")
        }
    }
}
