package me.aberrantfox.judgebot.conversations

import me.aberrantfox.judgebot.arguments.RuleWeightArg
import me.aberrantfox.judgebot.localization.Messages
import me.aberrantfox.judgebot.services.EmbedService
import me.aberrantfox.judgebot.dataclasses.Rule
import me.aberrantfox.judgebot.services.DatabaseService
import me.jakejmattson.discordkt.api.arguments.*
import me.jakejmattson.discordkt.api.dsl.conversation.Conversation
import me.jakejmattson.discordkt.api.dsl.conversation.conversation
import net.dv8tion.jda.api.entities.Guild

class RuleUpdateConversation(private val messages: Messages,
                             private val databaseService: DatabaseService,
                             private val embeds: EmbedService) : Conversation() {
    @Start
    fun ruleUpdateConversation(guild: Guild) = conversation(messages.CONVERSATION_EXIT_STRING) {
        val rules = databaseService.rules.getRules(guild.id)
        respond(embeds.embedRulesDetailed(guild.id))

        val ruleNumberToUpdate = promptUntil(
                argumentType = IntegerArg,
                prompt = messages.PROMPT_RULE_TO_UPDATE + messages.PROMPT_CONVERSATION_EXIT,
                isValid = { number -> rules.any { it.number == number } },
                error = messages.ERROR_RULE_NUMBER_NOT_EXISTS
        )

        val ruleToUpdate = rules.first { it.number == ruleNumberToUpdate }

        val updateNumber = promptMessage(BooleanArg(truthValue = "y", falseValue = "n"),
            messages.PROMPT_UPDATE_RULE_NUMBER)

        val ruleNumber = when {
            updateNumber -> promptUntil(
                    argumentType = IntegerArg,
                    prompt = messages.PROMPT_RULE_NUMBER,
                    isValid = { number -> !rules.any { it.number == number } },
                    error = messages.ERROR_RULE_NUMBER_EXISTS
            )
            else -> ruleToUpdate.number
        }

        val updateShortName = promptMessage(BooleanArg(truthValue = "Y", falseValue = "N"),
            messages.PROMPT_UPDATE_RULE_SHORTNAME)

        val ruleShortName = when {
            updateShortName -> promptUntil(
                    argumentType = AnyArg,
                    prompt = messages.PROMPT_RULE_SHORTNAME,
                    isValid = { shortName -> shortName.length < 16 && !rules.any { it.shortName == shortName } },
                    error = messages.ERROR_RULE_SHORTNAME_EXISTS
            )
            else -> ruleToUpdate.shortName
        }
        val updateTitle = promptMessage(BooleanArg(truthValue = "Y", falseValue = "N"),
            messages.PROMPT_UPDATE_RULE_TITLE)


        val ruleTitle = when {
            updateTitle -> promptMessage(EveryArg, messages.PROMPT_RULE_TITLE)
            else -> ruleToUpdate.title
        }

        val updateLink = promptMessage(BooleanArg("Update rule link", "Y", "N"),
            messages.PROMPT_UPDATE_RULE_LINK)

        val ruleLink = when {
            updateLink -> promptMessage(UrlArg, messages.PROMPT_RULE_LINK)
            else -> ruleToUpdate.link
        }

        val updateDescription = promptMessage(BooleanArg(truthValue = "Y", falseValue = "N"),
            messages.PROMPT_UPDATE_RULE_DESCRIPTION)

        val ruleDescription = when {
            updateDescription -> promptMessage(EveryArg, messages.PROMPT_RULE_DESCRIPTION)
            else -> ruleToUpdate.description
        }

        val updateWeight = promptMessage(BooleanArg(truthValue = "Y", falseValue = "N"),
            messages.PROMPT_UPDATE_RULE_WEIGHT)

        val ruleWeight = when {
            updateWeight -> promptMessage(RuleWeightArg, messages.PROMPT_RULE_WEIGHT)
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

            databaseService.rules.updateRule(updatedRule)
            respond(messages.RULE_UPDATED)
            respond(embeds.embedRuleDetailed(updatedRule))
        } else {
            respond(messages.NO_CHANGES_MADE)
        }
    }
}
