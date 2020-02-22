package me.aberrantfox.judgebot.conversations

import me.aberrantfox.judgebot.configuration.Constants
import me.aberrantfox.judgebot.localization.Messages
import me.aberrantfox.judgebot.localization.inject
import me.aberrantfox.judgebot.services.DatabaseService
import me.aberrantfox.judgebot.services.EmbedService
import me.aberrantfox.kjdautils.api.annotation.Convo
import me.aberrantfox.kjdautils.api.dsl.conversation
import me.aberrantfox.kjdautils.internal.arguments.BooleanArg
import me.aberrantfox.kjdautils.internal.arguments.IntegerArg
import me.aberrantfox.kjdautils.internal.arguments.WordArg

@Convo
fun ruleDeletionConversation(messages: Messages, dbService: DatabaseService, embeds: EmbedService) =
        conversation(name = Constants.RULE_DELETION_CONVERSATION) {
            val rules = dbService.getRules(guild.id)

            respond(embeds.embedRulesDetailed(guild.id))

            val ruleNumberToDelete = blockingPromptUntil(
                    argumentType = IntegerArg,
                    initialPrompt = { messages.PROMPT_RULE_TO_DELETE },
                    until = { number -> rules.any { it.number == number } },
                    errorMessage = { messages.ERROR_RULE_NUMBER_NOT_EXISTS }
            )

            val ruleToDelete = rules.first { it.number == ruleNumberToDelete }
            respond(messages.RULE_CHOSEN.inject(ruleNumberToDelete.toString()))
            respond(embeds.embedRuleDetailed(ruleToDelete))

            val sure = blockingPrompt(BooleanArg(truthValue = "y", falseValue = "n")) {
                messages.PROMPT_ARE_YOU_SURE
            }

            if (sure) {
                respond(messages.RULE_DELETED)
                dbService.deleteRule(ruleToDelete)
            } else {
                respond(messages.RULE_NOT_DELETED)
            }
}

