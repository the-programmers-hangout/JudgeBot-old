package me.aberrantfox.judgebot.conversations

import me.aberrantfox.judgebot.arguments.RuleWeightArg
import me.aberrantfox.judgebot.configuration.Rule
import me.aberrantfox.judgebot.localization.Messages
import me.aberrantfox.judgebot.services.DatabaseService
import me.aberrantfox.judgebot.services.EmbedService
import me.aberrantfox.kjdautils.api.dsl.Convo
import me.aberrantfox.kjdautils.api.dsl.conversation
import me.aberrantfox.kjdautils.internal.arguments.BooleanArg
import me.aberrantfox.kjdautils.internal.arguments.IntegerArg
import me.aberrantfox.kjdautils.internal.arguments.SentenceArg
import me.aberrantfox.kjdautils.internal.arguments.WordArg

@Convo
fun ruleUpdationConversation(messages: Messages, dbService: DatabaseService, embeds: EmbedService) =
        conversation(name = "Rule-Updation-Conversation") {
            val rules = dbService.getRules(guild.id)

            respond(embeds.embedRulesDetailed(guild.id))

            val ruleNumberToUpdate = blockingPromptUntil(
                    argumentType = IntegerArg,
                    initialPrompt = { messages.PROMPT_RULE_TO_UPDATE },
                    until = { number -> rules.any { it.number == number } },
                    errorMessage = { messages.ERROR_RULE_NUMBER_NOT_EXISTS }
            )

            val ruleToUpdate = rules.first { it.number == ruleNumberToUpdate }

            val updateNumber = blockingPrompt(BooleanArg(truthValue = "y", falseValue = "n")) {
                messages.PROMPT_UPDATE_RULE_NUMBER
            }
            val ruleNumber = when {
                updateNumber ->  blockingPromptUntil(
                        argumentType = IntegerArg,
                        initialPrompt = { messages.PROMPT_RULE_NUMBER },
                        until = { number -> !rules.any { it.number == number } },
                        errorMessage = { messages.ERROR_RULE_NUMBER_EXISTS }
                )
                else -> ruleToUpdate.number
            }

            val updateShortName = blockingPrompt(BooleanArg(truthValue = "Y", falseValue = "N")) {
                messages.PROMPT_UPDATE_RULE_SHORTNAME
            }
            val ruleShortName = when {
                updateShortName -> blockingPromptUntil(
                        argumentType = WordArg,
                        initialPrompt = { messages.PROMPT_RULE_SHORTNAME },
                        until = { shortName -> shortName.length < 16 && !rules.any { it.shortName == shortName } },
                        errorMessage = { messages.ERROR_RULE_SHORTNAME_EXISTS }
                )
                else -> ruleToUpdate.shortName
            }
            val updateTitle = blockingPrompt(BooleanArg(truthValue = "Y", falseValue = "N")) {
                messages.PROMPT_UPDATE_RULE_TITLE
            }
            val ruleTitle = when {
                updateTitle -> blockingPrompt(SentenceArg) { messages.PROMPT_RULE_TITLE }
                else -> ruleToUpdate.title
            }

            val updateDescription = blockingPrompt(BooleanArg(truthValue = "Y", falseValue = "N")) {
                messages.PROMPT_UPDATE_RULE_DESCRIPTION
            }
            val ruleDescription = when {
                updateDescription -> blockingPrompt(SentenceArg) { messages.PROMPT_RULE_DESCRIPTION }
                else -> ruleToUpdate.description
            }

            val updateWeight = blockingPrompt(BooleanArg(truthValue = "Y", falseValue = "N")) {
                messages.PROMPT_UPDATE_RULE_WEIGHT
            }
            val ruleWeight = when {
                updateWeight -> blockingPrompt(RuleWeightArg) { messages.PROMPT_RULE_WEIGHT }
                else -> ruleToUpdate.weight
            }

            val updateMade = updateNumber || updateShortName || updateTitle || updateDescription || updateWeight
            if (updateMade) {
                val updatedRule = Rule(
                        ruleToUpdate._id,
                        ruleToUpdate.guildId,
                        ruleNumber,
                        ruleShortName,
                        ruleTitle,
                        ruleDescription,
                        ruleWeight
                        )

                dbService.updateRule(updatedRule)
                respond(messages.RULE_UPDATED)
                respond(embeds.embedRuleDetailed(updatedRule))
            } else {
                respond(messages.NO_CHANGES_MADE)
            }
        }