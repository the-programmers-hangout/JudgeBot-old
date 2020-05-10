package me.aberrantfox.judgebot.commands

import me.aberrantfox.judgebot.arguments.LowerMemberArg
import me.aberrantfox.judgebot.configuration.BotConfiguration
import me.aberrantfox.judgebot.conversations.InfractionConversation
import me.aberrantfox.judgebot.dataclasses.PunishmentConfig
import me.aberrantfox.judgebot.dataclasses.PunishmentType
import me.aberrantfox.judgebot.extensions.requiredPermissionLevel
import me.aberrantfox.judgebot.services.*
import me.aberrantfox.judgebot.utility.buildUserStatusEmbed
import me.aberrantfox.kjdautils.api.annotation.CommandSet
import me.aberrantfox.kjdautils.api.dsl.command.commands
import me.aberrantfox.kjdautils.extensions.stdlib.toTimeString
import me.aberrantfox.kjdautils.internal.arguments.*
import me.aberrantfox.kjdautils.internal.services.ConversationResult
import me.aberrantfox.kjdautils.internal.services.ConversationService

@CommandSet("Infraction")
fun createInfractionCommands(conversationService: ConversationService,
                             config: BotConfiguration,
                             roleService: RoleService,
                             infractionService: InfractionService,
                             databaseService: DatabaseService,
                             badPfpService: BadPfpService) = commands {
    command("infract", "warn", "strike") {
        description = "Use this to issue an infraction to a user"
        requiresGuild = true
        requiredPermissionLevel = Permission.Staff
        execute(MemberArg()) {
            val response = when (conversationService.startConversation<InfractionConversation>(it.author, it.guild!!, it.args.first)) {
                ConversationResult.COMPLETE -> "Infraction completed."
                ConversationResult.EXITED -> "Infraction cancelled."
                ConversationResult.INVALID_USER -> "Cannot start a conversation with this user."
                ConversationResult.HAS_CONVO -> "This user already has a conversation."
                ConversationResult.CANNOT_DM -> "This user has DM's disabled."
            }
            it.respond(response)
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

    command("badpfp") {
        description = "Notifies the user that they should change their profile pic and applies a 30 minute mute. Bans the user if they don't change picture."
        requiresGuild = true
        requiredPermissionLevel = Permission.Moderator
        execute(BooleanArg("cancel", "apply", "cancel").makeOptional(true), LowerMemberArg) {
            val (cancel, target) = it.args
            val minutesUntilBan = 2L
            val timeLimit = 1000 * 60 * minutesUntilBan

            if (!cancel)
                when (badPfpService.hasBadPfp(target)) {
                    true -> {
                        badPfpService.cancelBadPfp(it.guild!!, target)
                        return@execute it.unsafeRespond("Badpfp cancelled for ${target.asMention} ")
                    }
                    false -> return@execute it.unsafeRespond("${target.asMention} does not have a an active badpfp")
                }

            val punishmentConfig = PunishmentConfig(PunishmentType.BadPfp, timeLimit)
            badPfpService.applyBadPfp(target, punishmentConfig, it.guild!!)
            it.unsafeRespond("${target.asMention} has been flagged for having a bad pfp and muted for $minutesUntilBan minutes.")
        }
    }

    command("punishmentInfo") {
        description = "View a user's points and next punishment level"
        requiredPermissionLevel = Permission.Administrator
        execute(MemberArg) {
            val user = databaseService.users.getOrCreateUser(it.args.first, it.guild!!.id)
            val punishment = infractionService.calculatePunishment(it.args.first, user, it.guild!!)
            val points = user.getGuildInfo(it.guild!!.id)!!.points
            it.respond("Points: **$points** \nNext Punishment: **${punishment?.punishment}** for **${punishment?.time?.toTimeString()}**")
        }
    }
}
