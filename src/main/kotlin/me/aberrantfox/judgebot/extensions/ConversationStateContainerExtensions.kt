package me.aberrantfox.judgebot.extensions

import me.aberrantfox.kjdautils.api.dsl.ConversationStateContainer
import me.aberrantfox.kjdautils.internal.arguments.SentenceArg

fun ConversationStateContainer.next() = blockingPromptUntil(
        argumentType = SentenceArg,
        initialPrompt = { "Type anything to continue" },
        until = { true },
        errorMessage = { "" }
)
