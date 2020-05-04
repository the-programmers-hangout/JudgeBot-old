package me.aberrantfox.judgebot.commands

import me.aberrantfox.judgebot.configuration.BotConfiguration
import me.aberrantfox.judgebot.conversations.InfractionConversation
import me.aberrantfox.judgebot.extensions.requiredPermissionLevel
import me.aberrantfox.judgebot.services.*
import me.aberrantfox.judgebot.utility.buildUserStatusEmbed
import me.aberrantfox.kjdautils.api.annotation.CommandSet
import me.aberrantfox.kjdautils.api.dsl.command.commands
import me.aberrantfox.kjdautils.internal.arguments.*
import me.aberrantfox.kjdautils.internal.services.ConversationService

@CommandSet("Infraction")
fun createInfractionCommands(conversationService: ConversationService,
                             config: BotConfiguration,
                             databaseService: DatabaseService) = commands {
    command("infract", "warn", "strike") {
        description = "Use this to issue an infraction to a user"
        requiresGuild = true
        requiredPermissionLevel = Permission.Staff
        execute(MemberArg()) {
            conversationService.startConversation<InfractionConversation>(it.author, it.guild!!, it.args.first)
        }
    }

    command("history", "h") {
        description = "Use this to view a user's record"
        requiresGuild = true
        requiredPermissionLevel = Permission.Staff
        execute(MemberArg) {
            val user = databaseService.users.getOrCreateUser(it.args.first, it.guild!!.id)
            val rules = databaseService.rules.getRules(it.guild!!.id)
            databaseService.users.incrementUserHistory(user, it.guild!!.id)
            it.respond(buildUserStatusEmbed(it.args.first, user, it.guild!!, config, rules, true))
        }
    }
}