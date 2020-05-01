package me.aberrantfox.judgebot.commands

import me.aberrantfox.judgebot.arguments.RuleArg
import me.aberrantfox.judgebot.configuration.Constants
import me.aberrantfox.judgebot.conversations.RuleCreationConversation
import me.aberrantfox.judgebot.conversations.RuleDeletionConversation
import me.aberrantfox.judgebot.conversations.RuleUpdateConversation
import me.aberrantfox.judgebot.extensions.requiredPermissionLevel
import me.aberrantfox.judgebot.localization.Messages
import me.aberrantfox.judgebot.services.DatabaseService
import me.aberrantfox.judgebot.services.EmbedService
import me.aberrantfox.judgebot.services.Permission
import me.aberrantfox.kjdautils.api.annotation.CommandSet
import me.aberrantfox.kjdautils.internal.services.ConversationService
import me.aberrantfox.kjdautils.api.dsl.command.commands
import me.aberrantfox.kjdautils.internal.arguments.*


@CommandSet("Rule Management")
fun createRulesManagementCommands(conversationService: ConversationService,
                         embeds: EmbedService,
                         messages: Messages) = commands {

    command("createRule") {
        description = messages.CREATE_RULE_DESCRIPTION
        requiresGuild = true
        requiredPermissionLevel = Permission.Administrator

        execute {
            conversationService.startConversation<RuleCreationConversation>(it.author, it.guild!!)
        }
    }
    command("deleteRule") {
        description = messages.DELETE_RULE_DESCRIPTION
        requiresGuild = true
        requiredPermissionLevel = Permission.Administrator

        execute {
            conversationService.startConversation<RuleDeletionConversation>(it.author, it.guild!!)
        }
    }
    command("rules") {
        description = messages.DISPLAY_RULES_DESCRIPTION
        requiresGuild = true
        requiredPermissionLevel = Permission.Staff

        execute {
            it.respond(embeds.embedRules(it.guild!!))
        }
    }
    command("ruleHeadings") {
        description = messages.DISPLAY_SHORT_RULES_DESCRIPTION
        requiresGuild = true
        requiredPermissionLevel = Permission.Staff

        execute {
            it.respond(embeds.embedRulesShort(it.guild!!))
        }
    }
    command("updateRule") {
        description = messages.UPDATE_RULE_DESCRIPTION
        requiresGuild = true
        requiredPermissionLevel = Permission.Administrator

        execute {
            conversationService.startConversation<RuleUpdateConversation>(it.author, it.guild!!)
        }
    }
    command("rule") {
        description = messages.DISPLAY_RULE_DESCRIPTION
        requiresGuild = true
        requiredPermissionLevel = Permission.Staff

        execute(RuleArg) {
            val rule = it.args.first
            it.respond(embeds.embedRule(rule))
        }
    }
}
