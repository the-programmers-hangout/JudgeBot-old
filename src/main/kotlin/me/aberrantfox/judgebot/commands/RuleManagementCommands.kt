package me.aberrantfox.judgebot.commands

import me.aberrantfox.judgebot.configuration.Constants
import me.aberrantfox.judgebot.localization.Messages
import me.aberrantfox.judgebot.services.DatabaseService
import me.aberrantfox.judgebot.services.EmbedService
import me.aberrantfox.kjdautils.api.dsl.command.CommandSet
import me.aberrantfox.kjdautils.internal.services.ConversationService
import me.aberrantfox.kjdautils.api.dsl.command.commands
import me.aberrantfox.kjdautils.internal.arguments.*


@CommandSet("Rule Management")
fun conversationCommands(conversationService: ConversationService,
                         embeds: EmbedService,
                         dbService: DatabaseService,
                         messages: Messages) = commands {

    command("createRule") {
        description = messages.CREATE_RULE_DESCRIPTION
        requiresGuild = true
        execute {
            conversationService.createConversation(it.author, it.guild!!, Constants.RULE_CREATION_CONVERSATION)
        }
    }
    command("deleteRule") {
        description = messages.DELETE_RULE_DESCRIPTION
        requiresGuild = true
        execute {
            conversationService.createConversation(it.author, it.guild!!, Constants.RULE_DELETION_CONVERSATION)
        }
    }
    command("rules") {
        description = messages.DISPLAY_RULES_DESCRIPTION
        requiresGuild = true
        execute {
            it.respond(embeds.embedRules(it.guild!!.id))
        }
    }
    command("updateRule") {
        description = messages.UPDATE_RULE_DESCRIPTION
        requiresGuild = true
        execute {
            conversationService.createConversation(it.author, it.guild!!, conversationName = Constants.RULE_UPDATE_CONVERSATION)
        }
    }
    command("rule") {
        description = messages.DISPLAY_RULE_DESCRIPTION
        requiresGuild = true
        // TODO: Should be refactored to RuleArg if support for services in args becomes a thing
        execute(IntegerArg or WordArg) {
            val rule = when (val input = it.args.first) {
                is Either.Left -> dbService.getRule(input.left, it.guild!!.id)
                is Either.Right -> dbService.getRule(input.right, it.guild!!.id)
            }
            if (rule == null) {
                it.respond(messages.ERROR_COULD_NOT_FIND_RULE)
            } else {
                it.respond(embeds.embedRule(rule))
            }
        }
    }
}
