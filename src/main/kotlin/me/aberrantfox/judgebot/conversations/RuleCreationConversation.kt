package me.aberrantfox.judgebot.conversations

import me.aberrantfox.judgebot.arguments.RuleWeightArg
import me.aberrantfox.judgebot.services.EmbedService
import me.aberrantfox.judgebot.localization.Messages
import me.aberrantfox.judgebot.localization.inject
import me.aberrantfox.judgebot.dataclasses.Rule
import me.aberrantfox.judgebot.services.DatabaseService
import me.jakejmattson.discordkt.api.arguments.*
import me.jakejmattson.discordkt.api.dsl.conversation.Conversation
import me.jakejmattson.discordkt.api.dsl.conversation.conversation
import net.dv8tion.jda.api.entities.Guild
import org.litote.kmongo.newId

class RuleCreationConversation(private val messages: Messages,
                               private val databaseService: DatabaseService,
                               private val embeds: EmbedService): Conversation() {
    @Start
    fun ruleCreationConversation(guild: Guild) = conversation(messages.CONVERSATION_EXIT_STRING) {
        val guildRules = databaseService.rules.getRulesSortedByNumber(guild.id)
        val lastUsedRuleNumber = if (guildRules.isNotEmpty()) guildRules.last().number else 0

        val useNextRuleNumber = promptMessage(BooleanArg(truthValue = "Y", falseValue = "N"),
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
        )

        val ruleNumber = when {
            useNextRuleNumber -> lastUsedRuleNumber + 1
            else -> promptUntil(
                    argumentType = IntegerArg,
                    prompt = messages.PROMPT_RULE_NUMBER,
                    isValid = { number -> !guildRules.any { it.number == number } },
                    error = messages.ERROR_RULE_NUMBER_EXISTS
            )
        }

        val ruleShortName = promptUntil(
                argumentType = AnyArg,
                prompt = messages.PROMPT_RULE_SHORTNAME,
                isValid = { shortName -> shortName.length < 16 && !guildRules.any { it.shortName == shortName } },
                error = messages.ERROR_RULE_SHORTNAME_EXISTS
        )

        val ruleTitle = promptMessage(EveryArg, messages.PROMPT_RULE_TITLE)

        val addLink = promptMessage(BooleanArg("Add Link to rule?", "Y", "N"),
            messages.PROMPT_UPDATE_RULE_LINK)

        val ruleLink = when {
            addLink -> promptMessage(UrlArg, messages.PROMPT_RULE_LINK)
            else -> ""
        }

        val ruleDescription = promptMessage(EveryArg, messages.PROMPT_RULE_DESCRIPTION)

        val ruleWeight = promptMessage(RuleWeightArg, messages.PROMPT_RULE_WEIGHT)

        val newRule = Rule(newId(), guild.id, ruleNumber, ruleShortName, ruleTitle, ruleDescription, ruleLink, ruleWeight)

        respond(messages.RULE_CREATED)
        respond(embeds.embedRuleDetailed(newRule))
        databaseService.rules.addRule(newRule)
    }
}