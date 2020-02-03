package me.aberrantfox.judgebot.conversations

import me.aberrantfox.judgebot.configuration.GuildConfiguration
import me.aberrantfox.judgebot.configuration.Rule
import me.aberrantfox.judgebot.localization.Messages
import me.aberrantfox.kjdautils.api.dsl.Convo
import me.aberrantfox.kjdautils.api.dsl.conversation
import me.aberrantfox.kjdautils.internal.arguments.IntegerArg
import me.aberrantfox.kjdautils.internal.arguments.SentenceArg
import me.aberrantfox.kjdautils.internal.arguments.WordArg

@Convo
fun ruleCreationConversation(messages: Messages, guildConfiguration: GuildConfiguration) = conversation(name = "Rule-Creation-Conversation") {
    //TODO: Make a nice, working existence check for rules.
    val id = blockingPromptUntil(
            argumentType = WordArg,
            initialPrompt = { messages.PROMPT_RULE_ID },
            until = { false },
            errorMessage = { messages.ERROR_RULE_ID_EXISTS }
    )

    val title = blockingPrompt(SentenceArg) { messages.PROMPT_RULE_TITLE }

    val description = blockingPrompt(SentenceArg) { messages.PROMPT_RULE_DESCRIPTION}

    val weight = blockingPromptUntil(
            argumentType = IntegerArg,
            initialPrompt = { messages.PROMPT_RULE_WEIGHT },
            until = { it > 0 },
            errorMessage = { messages.ERROR_RULE_WEIGHT_TOO_LOW }
    )

    val rule = Rule(id, title, description, weight)
    //TODO: Figure out how to add the rule to a guild
}