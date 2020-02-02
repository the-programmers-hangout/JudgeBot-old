package me.aberrantfox.judgebot.conversations

import me.aberrantfox.judgebot.extensions.next
import me.aberrantfox.judgebot.localization.Messages
import me.aberrantfox.kjdautils.api.dsl.Convo
import me.aberrantfox.kjdautils.api.dsl.conversation
import me.aberrantfox.kjdautils.internal.arguments.UserArg

@Convo
fun createInfractionConversation(messages: Messages) = conversation("Infraction-Conversation") {
    val id = blockingPrompt(UserArg) { messages.PROMPT_USER_ID_INFRACTION }

    // TODO: Display history at this point.

    next()

    // TODO: Display server rules
}