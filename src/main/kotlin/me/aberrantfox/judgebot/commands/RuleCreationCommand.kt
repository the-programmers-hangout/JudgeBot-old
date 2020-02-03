package me.aberrantfox.judgebot.commands

import me.aberrantfox.kjdautils.api.dsl.command.CommandSet
import me.aberrantfox.kjdautils.internal.services.ConversationService
import me.aberrantfox.kjdautils.api.dsl.command.commands


@CommandSet("Rule Management")
fun conversationCommands(conversationService: ConversationService) = commands {
    command("NewRule") {
        description = "Use this to create new rules for your guild."
        requiresGuild = true
        execute {
            conversationService.createConversation(it.author, it.guild!!, "Rule-Creation-Conversation")
        }
    }
}