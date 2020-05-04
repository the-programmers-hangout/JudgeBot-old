package me.aberrantfox.judgebot.conversations

import me.aberrantfox.judgebot.configuration.BotConfiguration
import me.aberrantfox.judgebot.extensions.next
import me.aberrantfox.judgebot.localization.Messages
import me.aberrantfox.judgebot.services.*
import me.aberrantfox.judgebot.dataclasses.Infraction
import me.aberrantfox.judgebot.dataclasses.InfractionWeight
import me.aberrantfox.judgebot.dataclasses.convertToInfractionType
import me.aberrantfox.judgebot.utility.buildUserStatusEmbed
import me.aberrantfox.kjdautils.api.dsl.Conversation
import me.aberrantfox.kjdautils.api.dsl.conversation
import me.aberrantfox.kjdautils.api.getInjectionObject
import me.aberrantfox.kjdautils.internal.arguments.*
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.Member

val infractionChoiceArg = ChoiceArg("InfractionTypes", "Note", "Borderline", "Lightly", "Clearly", "Harshly")

class InfractionConversation(): Conversation() {
    @Start
    fun createInfractionConversation(guild: Guild, targetMember: Member) = conversation {
        val messages = discord.getInjectionObject<Messages>()!!
        val infractionService = discord.getInjectionObject<InfractionService>()!!
        val databaseService = discord.getInjectionObject<DatabaseService>()!!
        val config = discord.getInjectionObject<BotConfiguration>()!!

        val userRecord = databaseService.users.getOrCreateUser(targetMember, guild.id)
        var addPersonalNote: Boolean = false
        var personalNote: String? = null

        databaseService.users.incrementUserHistory(userRecord, guild.id)
        respond(buildUserStatusEmbed(targetMember, userRecord, guild, config, databaseService.rules.getRules(guild.id), true))

        val rules = databaseService.rules.getRules(guild.id)
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
        infractionService.infract(targetMember.user, guild, userRecord, infraction)

        respond(buildUserStatusEmbed(targetMember, userRecord, guild, config, rules, true))

        next()
    }
}
