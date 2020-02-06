package me.aberrantfox.judgebot.commands

import me.aberrantfox.judgebot.services.EmbedService
import me.aberrantfox.kjdautils.api.dsl.command.CommandSet
import me.aberrantfox.kjdautils.internal.services.ConversationService
import me.aberrantfox.kjdautils.api.dsl.command.commands


@CommandSet("Rule Management")
fun conversationCommands(conversationService: ConversationService,
                         embeds: EmbedService)
        = commands {

    command("createRule") {
        description = "Use this to create new rules for your guild."
        requiresGuild = true
        execute {
            conversationService.createConversation(it.author, it.guild!!, "Rule-Creation-Conversation")
        }
    }
    command("deleteRule") {
        description = "Use this to delete rules for your guild."
        requiresGuild = true
        execute {
            conversationService.createConversation(it.author, it.guild!!, "Rule-Deletion-Conversation")
        }
    }
    command("rules", "showRules", "readRules") {
        description = "Displays all the rules and their weights."
        requiresGuild = true
        execute {
            it.respond(embeds.embedRules(it.guild!!.id))
        }
    }
    command("updateRule") {
        description = "Update a rule for this guild."
        requiresGuild = true
        execute {
            conversationService.createConversation(it.author, it.guild!!, conversationName = "Rule-Updation-Conversation")
        }
    }
}