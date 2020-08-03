package me.aberrantfox.judgebot.conversations

import me.aberrantfox.judgebot.configuration.Configuration
import me.aberrantfox.judgebot.extensions.next
import me.aberrantfox.judgebot.localization.Messages
import me.aberrantfox.judgebot.services.*
import me.aberrantfox.judgebot.dataclasses.Infraction
import me.aberrantfox.judgebot.dataclasses.InfractionType
import me.aberrantfox.judgebot.dataclasses.convertToInfractionType
import me.aberrantfox.judgebot.utility.buildUserStatusMenu
import me.jakejmattson.discordkt.api.arguments.*
import me.jakejmattson.discordkt.api.dsl.conversation.Conversation
import me.jakejmattson.discordkt.api.dsl.conversation.conversation
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.Member

val infractionChoiceArg = ChoiceArg("InfractionTypes", "Note", "Borderline", "Light", "Clear", "Harsh")

class InfractionConversation(private val messages: Messages,
                             private val infractionService: InfractionService,
                             private val databaseService: DatabaseService,
                             private val config: Configuration): Conversation() {
    @Start
    fun createInfractionConversation(guild: Guild, targetMember: Member) = conversation(messages.CONVERSATION_EXIT_STRING) {
        val userRecord = databaseService.users.getOrCreateUser(targetMember, guild.id)
        var addPersonalNote: Boolean = false
        var personalNote: String? = ""

        databaseService.users.incrementUserHistory(userRecord, guild.id)
        respond(buildUserStatusMenu(targetMember, userRecord, guild, config, databaseService.rules.getRules(guild.id), true))

        val rules = databaseService.rules.getRules(guild.id)
        val ruleNumberChosen = promptUntil(
                argumentType = IntegerArg,
                prompt = messages.PROMPT_INFRACTION_RULE_BROKEN + messages.PROMPT_CONVERSATION_EXIT,
                isValid = { number -> rules.any { it.number == number } },
                error = messages.ERROR_RULE_NUMBER_NOT_EXISTS
        )

        var infractionChoice = promptMessage(infractionChoiceArg, messages.PROMPT_USER_INFRACTION_TYPE)
        val infractionType = convertToInfractionType(infractionChoice)
        val infractionDetails: String = promptMessage(EveryArg, messages.PROMPT_INFRACTION_DETAILS)

        if (infractionType != InfractionType.Note) {
            addPersonalNote = promptMessage(BooleanArg("Add Personal Note", "yes", "no"),
            messages.PROMPT_USER_ADD_PERSONAL_NOTE)
        }

        if (addPersonalNote) {
            personalNote = promptMessage(EveryArg, messages.PROMPT_PERSONAL_NOTE)
        }

        if (infractionType == InfractionType.Note) {
            userRecord.addNote(infractionDetails, this.user.id, guild)
            databaseService.users.updateUser(userRecord)
            respond(buildUserStatusMenu(targetMember, userRecord, guild, config, rules, true))
        } else {
            val infraction = Infraction(this.user.id, infractionDetails, infractionType!!, guild.id, personalNote, ruleNumberChosen)
            infractionService.infract(targetMember, guild, userRecord, infraction)
            respond(buildUserStatusMenu(targetMember, userRecord, guild, config, rules, true))
        }

        next()
    }
}
