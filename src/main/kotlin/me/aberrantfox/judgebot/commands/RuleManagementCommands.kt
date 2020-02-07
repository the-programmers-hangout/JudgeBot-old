package me.aberrantfox.judgebot.commands

import me.aberrantfox.judgebot.configuration.Rule
import me.aberrantfox.judgebot.services.DatabaseService
import me.aberrantfox.judgebot.services.EmbedService
import me.aberrantfox.kjdautils.api.dsl.command.CommandSet
import me.aberrantfox.kjdautils.internal.services.ConversationService
import me.aberrantfox.kjdautils.api.dsl.command.commands
import me.aberrantfox.kjdautils.internal.arguments.*
import me.aberrantfox.kjdautils.internal.command.ArgumentType


@CommandSet("Rule Management")
fun conversationCommands(conversationService: ConversationService, embeds: EmbedService, dbService: DatabaseService)
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
    command("rules") {
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
    command("rule") {
        description = "Display a given rule."
        requiresGuild = true
        execute(IntegerArg or WordArg) {
            val rule: Rule?
            rule = when (val input = it.args.first) {
                is Either.Left -> dbService.getRule(input.left, it.guild!!.id)
                is Either.Right -> dbService.getRule(input.right, it.guild!!.id)
            }
            if (rule == null) {
                it.respond("Could not find rule.")
            } else {
                it.respond(embeds.embedRule(rule))
            }
        }
    }
}