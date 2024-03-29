package me.aberrantfox.judgebot.commands

import me.aberrantfox.judgebot.arguments.RuleArg
import me.aberrantfox.judgebot.conversations.RuleCreationConversation
import me.aberrantfox.judgebot.conversations.RuleDeletionConversation
import me.aberrantfox.judgebot.conversations.RuleUpdateConversation
import me.aberrantfox.judgebot.extensions.requiredPermissionLevel
import me.aberrantfox.judgebot.localization.Messages
import me.aberrantfox.judgebot.services.EmbedService
import me.aberrantfox.judgebot.services.Permission
import me.jakejmattson.discordkt.api.annotations.CommandSet
import me.jakejmattson.discordkt.api.dsl.command.commands
import me.jakejmattson.discordkt.api.services.ConversationService

@CommandSet("Rules")
fun createRulesManagementCommands(conversationService: ConversationService,
                                  embeds: EmbedService,
                                  messages: Messages) = commands {

    command("createRule") {
        description = messages.CREATE_RULE_DESCRIPTION
        requiresGuild = true
        requiredPermissionLevel = Permission.Administrator

        execute {
            conversationService.startPrivateConversation<RuleCreationConversation>(it.author, it.guild!!)
        }
    }
    command("deleteRule") {
        description = messages.DELETE_RULE_DESCRIPTION
        requiresGuild = true
        requiredPermissionLevel = Permission.Administrator

        execute {
            conversationService.startPrivateConversation<RuleDeletionConversation>(it.author, it.guild!!)
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
    command("ruleList") {
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
            conversationService.startPrivateConversation<RuleUpdateConversation>(it.author, it.guild!!)
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
