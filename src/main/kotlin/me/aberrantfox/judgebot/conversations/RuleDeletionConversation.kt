package me.aberrantfox.judgebot.conversations

import me.aberrantfox.judgebot.localization.Messages
import me.aberrantfox.judgebot.localization.inject
import me.aberrantfox.judgebot.services.DatabaseService
import me.aberrantfox.judgebot.services.EmbedService
import me.aberrantfox.kjdautils.api.dsl.Conversation
import me.aberrantfox.kjdautils.api.dsl.conversation
import me.aberrantfox.kjdautils.api.getInjectionObject
import me.aberrantfox.kjdautils.internal.arguments.BooleanArg
import me.aberrantfox.kjdautils.internal.arguments.IntegerArg
import net.dv8tion.jda.api.entities.Guild

class RuleDeletionConversation() : Conversation() {
    @Start
    fun ruleDeletionConversation(guild: Guild) = conversation {
        val messages = discord.getInjectionObject<Messages>()!!
        val databaseService = discord.getInjectionObject<DatabaseService>()!!
        val embeds = discord.getInjectionObject<EmbedService>()!!

        val rules = databaseService.rules.getRules(guild.id)
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
            databaseService.rules.deleteRule(ruleToDelete)
        } else {
            respond(messages.RULE_NOT_DELETED)
        }
    }
}

