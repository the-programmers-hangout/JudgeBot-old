package me.aberrantfox.judgebot.extensions

import me.jakejmattson.discordkt.api.arguments.EveryArg
import me.jakejmattson.discordkt.api.dsl.conversation.ConversationStateContainer


fun ConversationStateContainer.next() = promptUntil(
        argumentType = EveryArg,
        prompt = "Type anything to continue",
        isValid = { true },
        error = ""
)
