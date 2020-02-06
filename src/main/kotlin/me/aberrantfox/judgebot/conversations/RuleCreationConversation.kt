package me.aberrantfox.judgebot.conversations

import me.aberrantfox.judgebot.configuration.*
import me.aberrantfox.judgebot.services.EmbedService
import me.aberrantfox.judgebot.localization.Messages
import me.aberrantfox.judgebot.services.DatabaseService
import me.aberrantfox.kjdautils.api.dsl.Convo
import me.aberrantfox.kjdautils.api.dsl.conversation
import me.aberrantfox.kjdautils.internal.arguments.IntegerArg
import me.aberrantfox.kjdautils.internal.arguments.SentenceArg
import me.aberrantfox.kjdautils.internal.arguments.WordArg
import org.litote.kmongo.newId

@Convo
fun ruleCreationConversation(messages: Messages, dbService: DatabaseService, embeds: EmbedService) =
        conversation(name = "Rule-Creation-Conversation") {
            val rules = dbService.getRules(guild.id)

            val number = blockingPromptUntil(
                    argumentType = IntegerArg,
                    initialPrompt = { messages.PROMPT_RULE_NUMBER },
                    until = { number -> !rules.any { it.number == number } },
                    errorMessage = { messages.ERROR_RULE_NUMBER_EXISTS }
            )

            val shortName = blockingPromptUntil(
                    argumentType = WordArg,
                    initialPrompt = { messages.PROMPT_RULE_SHORTNAME },
                    until = { shortName -> shortName.length < 16 && !rules.any { it.shortName == shortName } },
                    errorMessage = { messages.ERROR_RULE_SHORTNAME_EXISTS }
            )

            val title = blockingPrompt(SentenceArg) { messages.PROMPT_RULE_TITLE }

            val description = blockingPrompt(SentenceArg) { messages.PROMPT_RULE_DESCRIPTION }

            val weight = blockingPromptUntil(
                    argumentType = IntegerArg,
                    initialPrompt = { messages.PROMPT_RULE_WEIGHT },
                    until = { it in 1..5 },
                    errorMessage = { messages.ERROR_RULE_WEIGHT_TOO_LOW }
            )

            val rule = Rule(newId(), guild.id, number, shortName, title, description, weight)
            respond("Rule created!")
            respond(embeds.embedRuleDetailed(rule))
            dbService.addRule(rule)
        }