package me.aberrantfox.judgebot.conversations

import me.aberrantfox.judgebot.configuration.InfractionWeight
import me.aberrantfox.judgebot.configuration.convertToInfractionType
import me.aberrantfox.judgebot.extensions.next
import me.aberrantfox.judgebot.localization.Messages
import me.aberrantfox.judgebot.services.DatabaseService
import me.aberrantfox.kjdautils.api.dsl.Convo
import me.aberrantfox.kjdautils.api.dsl.conversation
import me.aberrantfox.kjdautils.internal.arguments.BooleanArg
import me.aberrantfox.kjdautils.internal.arguments.UserArg
import me.aberrantfox.kjdautils.internal.arguments.ChoiceArg
import me.aberrantfox.kjdautils.internal.arguments.SentenceArg

@Convo
fun createInfractionConversation(messages: Messages, databaseService: DatabaseService) = conversation("Infraction-Conversation") {
    val id = blockingPrompt(UserArg) { messages.PROMPT_USER_ID_INFRACTION }
    val userRecord = databaseService.getOrCreateUserRecord(id)
    var addPersonalNote: Boolean = false

    this.respond(databaseService.getUserHistory(id, userRecord))

    // TODO: Display server rules here

    var infractionChoice = blockingPrompt(
            ChoiceArg("InfractionTypes", "Note", "Borderline", "Lightly", "Clearly", "Harshly"))
    { messages.PROMPT_USER_INFRACTION_TYPE }

    val  infractionType  = convertToInfractionType(infractionChoice)

    if(infractionType != InfractionWeight.Note) {
        addPersonalNote = blockingPrompt(BooleanArg("Add Personal Note", "yes", "no"))
        { messages.PROMPT_USER_ADD_PERSONAL_NOTE }
    }

    if(addPersonalNote) {
        val personalNote: String = blockingPrompt(SentenceArg) {messages.PROMPT_PERSONAL_NOTE}
    }

    val infractionDetails: String = blockingPrompt(SentenceArg) { messages.PROMPT_INFRACTION_DETAILS }

    // TODO: Implement Infraction logic

    this.respond(databaseService.getUserHistory(id, userRecord))

    next()
}
