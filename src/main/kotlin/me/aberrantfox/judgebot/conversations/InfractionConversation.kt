package me.aberrantfox.judgebot.conversations

import me.aberrantfox.judgebot.extensions.next
import me.aberrantfox.judgebot.localization.Messages
import me.aberrantfox.judgebot.services.*
import me.aberrantfox.judgebot.dataclasses.Infraction
import me.aberrantfox.judgebot.dataclasses.InfractionWeight
import me.aberrantfox.judgebot.dataclasses.convertToInfractionType
import me.aberrantfox.kjdautils.api.dsl.Conversation
import me.aberrantfox.kjdautils.api.dsl.conversation
import me.aberrantfox.kjdautils.api.getInjectionObject
import me.aberrantfox.kjdautils.internal.arguments.*
import net.dv8tion.jda.api.entities.Guild

val infractionChoiceArg = ChoiceArg("InfractionTypes", "Note", "Borderline", "Lightly", "Clearly", "Harshly")

class InfractionConversation(): Conversation() {
    @Start
    fun createInfractionConversation(guild: Guild) = conversation {
        val messages = discord.getInjectionObject<Messages>()!!
        val infractionService = discord.getInjectionObject<InfractionService>()!!
        val userService = discord.getInjectionObject<UserService>()!!
        val ruleService = discord.getInjectionObject<RuleService>()!!

        val id = blockingPromptUntil(
                UserArg,
                { messages.PROMPT_USER_ID_INFRACTION },
                { user -> guild.isMember(user) },
                { messages.ERROR_USER_NOT_IN_GUILD }
        )

        val userRecord = userService.getOrCreateUserRecord(id, guild.id)
        var addPersonalNote: Boolean = false
        var personalNote: String? = null

        respond(userService.getUserHistory(id, userRecord, guild, true))

        val rules = ruleService.getRules(guild.id)
        val ruleNumberChosen = blockingPromptUntil(
                argumentType = IntegerArg,
                initialPrompt = { messages.PROMPT_INFRACTION_RULE_BROKEN },
                until = { number -> rules.any { it.number == number } },
                errorMessage = { messages.ERROR_RULE_NUMBER_NOT_EXISTS }
        )

        var infractionChoice = blockingPrompt(infractionChoiceArg) { messages.PROMPT_USER_INFRACTION_TYPE }
        val infractionType = convertToInfractionType(infractionChoice)
        val infractionDetails: String = blockingPrompt(SentenceArg) { messages.PROMPT_INFRACTION_DETAILS }

        if (infractionType != InfractionWeight.Note) {
            addPersonalNote = blockingPrompt(BooleanArg("Add Personal Note", "yes", "no"))
            { messages.PROMPT_USER_ADD_PERSONAL_NOTE }
        }

        if (addPersonalNote) {
            personalNote = blockingPrompt(SentenceArg) { messages.PROMPT_PERSONAL_NOTE }
        }

        val infraction = Infraction(this.user.name, infractionDetails, infractionType!!, guild.id, personalNote, ruleNumberChosen)
        infractionService.infract(id, guild, userRecord, infraction)

        respond(userService.getUserHistory(id, userRecord, guild, false))

        next()
    }
}
