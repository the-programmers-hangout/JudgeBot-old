package me.aberrantfox.judgebot.conversations

import me.aberrantfox.judgebot.arguments.RuleWeightArg
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
        conversation(name = Constants.RULE_CREATION_CONVERSATION) {
            val guildRules = dbService.getRules(guild.id)

            val ruleNumber = blockingPromptUntil(
                    argumentType = IntegerArg,
                    initialPrompt = { messages.PROMPT_RULE_NUMBER },
                    until = { number -> !guildRules.any { it.number == number } },
                    errorMessage = { messages.ERROR_RULE_NUMBER_EXISTS }
            )

            val ruleShortName = blockingPromptUntil(
                    argumentType = WordArg,
                    initialPrompt = { messages.PROMPT_RULE_SHORTNAME },
                    until = { shortName -> shortName.length < 16 && !guildRules.any { it.shortName == shortName } },
                    errorMessage = { messages.ERROR_RULE_SHORTNAME_EXISTS }
            )

            val ruleTitle = blockingPrompt(SentenceArg) { messages.PROMPT_RULE_TITLE }

            val ruleDescription = blockingPrompt(SentenceArg) { messages.PROMPT_RULE_DESCRIPTION }

            val ruleWeight = blockingPrompt(RuleWeightArg) { messages.PROMPT_RULE_WEIGHT }

            val newRule = Rule(newId(), guild.id, ruleNumber, ruleShortName, ruleTitle, ruleDescription, ruleWeight)
            respond(messages.RULE_CREATED)
            respond(embeds.embedRuleDetailed(newRule))
            dbService.addRule(newRule)
        }