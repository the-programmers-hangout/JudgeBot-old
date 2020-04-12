package me.aberrantfox.judgebot.conversations

import me.aberrantfox.judgebot.arguments.RuleWeightArg
import me.aberrantfox.judgebot.configuration.Constants
import me.aberrantfox.judgebot.localization.Messages
import me.aberrantfox.judgebot.services.EmbedService
import me.aberrantfox.judgebot.services.RuleService
import me.aberrantfox.judgebot.services.database.dataclasses.Rule
import me.aberrantfox.kjdautils.api.annotation.Convo
import me.aberrantfox.kjdautils.api.dsl.conversation
import me.aberrantfox.kjdautils.internal.arguments.*

@Convo
fun ruleUpdateConversation(messages: Messages, ruleService: RuleService, embeds: EmbedService) =
        conversation(name = Constants.RULE_UPDATE_CONVERSATION) {
            val rules = ruleService.getRules(guild.id)

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

            val updateLink = blockingPrompt(BooleanArg("Update rule link", "Y", "N"))
            { messages.PROMPT_UPDATE_RULE_LINK }

            val ruleLink = when {
                updateLink -> blockingPrompt(UrlArg) { messages.PROMPT_RULE_LINK }
                else -> ruleToUpdate.link
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

            val updateMade = updateNumber || updateShortName || updateTitle || updateDescription || updateLink || updateWeight
            if (updateMade) {
                val updatedRule = Rule(
                        ruleToUpdate._id,
                        ruleToUpdate.guildId,
                        ruleNumber,
                        ruleShortName,
                        ruleTitle,
                        ruleDescription,
                        ruleLink,
                        ruleWeight
                        )

                ruleService.updateRule(updatedRule)
                respond(messages.RULE_UPDATED)
                respond(embeds.embedRuleDetailed(updatedRule))
            } else {
                respond(messages.NO_CHANGES_MADE)
            }
        }
