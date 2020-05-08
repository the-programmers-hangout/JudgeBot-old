package me.aberrantfox.judgebot.conversations

import me.aberrantfox.judgebot.arguments.RuleWeightArg
import me.aberrantfox.judgebot.services.EmbedService
import me.aberrantfox.judgebot.localization.Messages
import me.aberrantfox.judgebot.localization.inject
import me.aberrantfox.judgebot.dataclasses.Rule
import me.aberrantfox.judgebot.services.DatabaseService
import me.aberrantfox.kjdautils.api.dsl.Conversation
import me.aberrantfox.kjdautils.api.dsl.conversation
import me.aberrantfox.kjdautils.internal.arguments.*
import net.dv8tion.jda.api.entities.Guild
import org.litote.kmongo.newId

class RuleCreationConversation(private val messages: Messages,
                               private val databaseService: DatabaseService,
                               private val embeds: EmbedService): Conversation() {
    @Start
    fun ruleCreationConversation(guild: Guild) = conversation(messages.CONVERSATION_EXIT_STRING) {
        val guildRules = databaseService.rules.getRulesSortedByNumber(guild.id)
        val lastUsedRuleNumber = if (guildRules.isNotEmpty()) guildRules.last().number else 0

        val useNextRuleNumber = blockingPrompt(BooleanArg(truthValue = "Y", falseValue = "N")) {
            if (guildRules.isNotEmpty()) {
                messages.PROMPT_USE_NEXT_RULE_NUMBER.inject(
                        lastUsedRuleNumber.toString(),
                        (lastUsedRuleNumber + 1).toString()
                ) + messages.PROMPT_CONVERSATION_EXIT
            } else {
                messages.PROMPT_CREATE_FIRST_RULE.inject(
                        lastUsedRuleNumber.toString()
                )
            }
        }

        val ruleNumber = when {
            useNextRuleNumber -> lastUsedRuleNumber + 1
            else -> blockingPromptUntil(
                    argumentType = IntegerArg,
                    initialPrompt = { messages.PROMPT_RULE_NUMBER },
                    until = { number -> !guildRules.any { it.number == number } },
                    errorMessage = { messages.ERROR_RULE_NUMBER_EXISTS }
            )
        }

        val ruleShortName = blockingPromptUntil(
                argumentType = WordArg,
                initialPrompt = { messages.PROMPT_RULE_SHORTNAME },
                until = { shortName -> shortName.length < 16 && !guildRules.any { it.shortName == shortName } },
                errorMessage = { messages.ERROR_RULE_SHORTNAME_EXISTS }
        )

        val ruleTitle = blockingPrompt(SentenceArg) { messages.PROMPT_RULE_TITLE }

        val addLink = blockingPrompt(BooleanArg("Add Link to rule?", "Y", "N"))
        { messages.PROMPT_UPDATE_RULE_LINK }

        val ruleLink = when {
            addLink -> blockingPrompt(UrlArg) { messages.PROMPT_RULE_LINK }
            else -> ""
        }

        val ruleDescription = blockingPrompt(SentenceArg) { messages.PROMPT_RULE_DESCRIPTION }

        val ruleWeight = blockingPrompt(RuleWeightArg) { messages.PROMPT_RULE_WEIGHT }

        val newRule = Rule(newId(), guild.id, ruleNumber, ruleShortName, ruleTitle, ruleDescription, ruleLink, ruleWeight)

        respond(messages.RULE_CREATED)
        respond(embeds.embedRuleDetailed(newRule))
        databaseService.rules.addRule(newRule)
    }
}