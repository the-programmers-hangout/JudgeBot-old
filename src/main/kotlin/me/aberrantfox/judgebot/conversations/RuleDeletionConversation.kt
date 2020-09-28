package me.aberrantfox.judgebot.conversations

import me.aberrantfox.judgebot.localization.Messages
import me.aberrantfox.judgebot.localization.inject
import me.aberrantfox.judgebot.services.DatabaseService
import me.aberrantfox.judgebot.services.EmbedService
import me.jakejmattson.discordkt.api.arguments.*
import me.jakejmattson.discordkt.api.dsl.conversation.Conversation
import me.jakejmattson.discordkt.api.dsl.conversation.conversation
import net.dv8tion.jda.api.entities.Guild

class RuleDeletionConversation(private val messages: Messages,
                               private val databaseService: DatabaseService,
                               private val embeds: EmbedService) : Conversation() {
    @Start
    fun ruleDeletionConversation(guild: Guild) = conversation(messages.CONVERSATION_EXIT_STRING) {
        val rules = databaseService.rules.getRules(guild.id)
        respond(embeds.embedRulesDetailed(guild.id))

        val ruleNumberToDelete = promptUntil(
                argumentType = IntegerArg,
                prompt = messages.PROMPT_RULE_TO_DELETE + messages.PROMPT_CONVERSATION_EXIT,
                isValid = { number -> rules.any { it.number == number } },
                error = messages.ERROR_RULE_NUMBER_NOT_EXISTS
        )

        val ruleToDelete = rules.first { it.number == ruleNumberToDelete }
        respond(messages.RULE_CHOSEN.inject(ruleNumberToDelete.toString()))
        respond(embeds.embedRuleDetailed(ruleToDelete))

        val sure = promptMessage(BooleanArg(truthValue = "y", falseValue = "n"),
            messages.PROMPT_ARE_YOU_SURE
        )

        if (sure) {
            respond(messages.RULE_DELETED)
            databaseService.rules.deleteRule(ruleToDelete)
        } else {
            respond(messages.RULE_NOT_DELETED)
        }
    }
}

