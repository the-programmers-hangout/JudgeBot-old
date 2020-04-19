package me.aberrantfox.judgebot.conversations

import me.aberrantfox.judgebot.extensions.next
import me.aberrantfox.judgebot.localization.Messages
import me.aberrantfox.judgebot.services.DatabaseService
import me.aberrantfox.judgebot.services.EmbedService
import me.aberrantfox.judgebot.services.InfractionService
import me.aberrantfox.judgebot.services.UserService
import me.aberrantfox.judgebot.services.database.dataclasses.Infraction
import me.aberrantfox.judgebot.services.database.dataclasses.InfractionWeight
import me.aberrantfox.judgebot.services.database.dataclasses.convertToInfractionType
import me.aberrantfox.kjdautils.api.annotation.Convo
import me.aberrantfox.kjdautils.api.dsl.conversation
import me.aberrantfox.kjdautils.internal.arguments.*

val infractionChoiceArg = ChoiceArg("InfractionTypes", "Note", "Borderline", "Lightly", "Clearly", "Harshly")

@Convo
fun createInfractionConversation(messages: Messages, infractionService: InfractionService, userService: UserService, embedService: EmbedService, ruleService: DatabaseService) = conversation("Infraction-Conversation") {
    val id = blockingPrompt(UserArg) { messages.PROMPT_USER_ID_INFRACTION }
    val userRecord = userService.getOrCreateUserRecord(id)
    var addPersonalNote: Boolean = false
    var personalNote: String? = null

    this.respond(userService.getUserHistory(id, userRecord, this.guild, true))
    
    val rules = ruleService.getRules(guild.id)
    val ruleNumberChosen = blockingPromptUntil(
            argumentType = IntegerArg,
            initialPrompt = { messages.PROMPT_INFRACTION_RULE_BROKEN },
            until = { number -> rules.any { it.number == number } },
            errorMessage = { messages.ERROR_RULE_NUMBER_NOT_EXISTS }
    )

    var infractionChoice = blockingPrompt(infractionChoiceArg) { messages.PROMPT_USER_INFRACTION_TYPE }
    val  infractionType  = convertToInfractionType(infractionChoice)
    val infractionDetails: String = blockingPrompt(SentenceArg) { messages.PROMPT_INFRACTION_DETAILS }

    if(infractionType != InfractionWeight.Note) {
        addPersonalNote = blockingPrompt(BooleanArg("Add Personal Note", "yes", "no"))
        { messages.PROMPT_USER_ADD_PERSONAL_NOTE }
    }

    if(addPersonalNote) {
        personalNote = blockingPrompt(SentenceArg) {messages.PROMPT_PERSONAL_NOTE}
    }

    val infraction = Infraction(this.user.name, infractionDetails, infractionType!!, this.guild.id, personalNote, ruleNumberChosen)
    infractionService.infract(id, userRecord, infraction)

    this.respond(userService.getUserHistory(id, userRecord, this.guild,false))

    next()
}
