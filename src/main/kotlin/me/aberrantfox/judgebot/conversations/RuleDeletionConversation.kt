package me.aberrantfox.judgebot.conversations

import me.aberrantfox.judgebot.localization.Messages
import me.aberrantfox.judgebot.services.DatabaseService
import me.aberrantfox.judgebot.services.EmbedService
import me.aberrantfox.kjdautils.api.dsl.Convo
import me.aberrantfox.kjdautils.api.dsl.conversation
import me.aberrantfox.kjdautils.internal.arguments.IntegerArg
import me.aberrantfox.kjdautils.internal.arguments.WordArg

@Convo
fun ruleDeletionConversation(messages: Messages, dbService: DatabaseService, embeds: EmbedService) =
        conversation(name = "Rule-Deletion-Conversation") {
            val rules = dbService.getRules(guild.id)

            respond(embeds.embedRulesDetailed(guild.id))

            val ruleNumberToDelete = blockingPromptUntil(
                    argumentType = IntegerArg,
                    initialPrompt = { messages.PROMPT_RULE_TO_DELETE },
                    until = { number -> rules.any { it.number == number } },
                    errorMessage = { messages.ERROR_RULE_NUMBER_NOT_EXISTS }
            )

            val ruleToDelete = rules.find { it.number == ruleNumberToDelete }!!
            respond("You have chosen to delete rule number ${ruleNumberToDelete}:")
            respond(embeds.embedRuleDetailed(ruleToDelete))

            val sure = blockingPromptUntil(
                    argumentType = WordArg,
                    initialPrompt = { messages.PROMPT_ARE_YOU_SURE },
                    until = { it.equals("y", ignoreCase = true) || it.equals("n", ignoreCase = true) },
                    errorMessage = { messages.ERROR_ANS_MUST_BE_Y_OR_N }
            )

            if (sure.equals("y", ignoreCase = true)) {
                respond(messages.RULE_DELETED)
                dbService.deleteRule(ruleToDelete)
            } else {
                respond(messages.RULE_NOT_DELETED)
            }
}
