package me.aberrantfox.judgebot.commands

import me.aberrantfox.judgebot.arguments.RuleArg
import me.aberrantfox.judgebot.configuration.Constants
import me.aberrantfox.judgebot.localization.Messages
import me.aberrantfox.judgebot.services.DatabaseService
import me.aberrantfox.judgebot.services.EmbedService
import me.aberrantfox.kjdautils.api.annotation.CommandSet
import me.aberrantfox.kjdautils.internal.services.ConversationService
import me.aberrantfox.kjdautils.api.dsl.command.commands
import me.aberrantfox.kjdautils.internal.arguments.*


@CommandSet("Rule Management")
fun createRulesManagementCommands(conversationService: ConversationService,
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
        execute(RuleArg) {
            val rule = it.args.first
            it.respond(embeds.embedRule(rule))
        }
    }
}
