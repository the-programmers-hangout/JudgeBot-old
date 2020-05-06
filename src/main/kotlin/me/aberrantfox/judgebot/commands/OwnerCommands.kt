package me.aberrantfox.judgebot.commands

import me.aberrantfox.judgebot.configuration.BotConfiguration
import me.aberrantfox.judgebot.extensions.requiredPermissionLevel
import me.aberrantfox.judgebot.services.Permission
import me.aberrantfox.judgebot.services.PrefixService
import me.aberrantfox.kjdautils.api.annotation.CommandSet
import me.aberrantfox.kjdautils.api.dsl.command.commands
import me.aberrantfox.kjdautils.internal.arguments.RoleArg
import me.aberrantfox.kjdautils.internal.arguments.TextChannelArg
import me.aberrantfox.kjdautils.internal.arguments.WordArg
import me.aberrantfox.kjdautils.internal.services.PersistenceService

@CommandSet("Owner")
fun createOwnerCommands(configuration: BotConfiguration, prefixService: PrefixService, persistenceService: PersistenceService) = commands {
    command("setPrefix") {
        description = "Set the bot's prefix."
        requiredPermissionLevel = Permission.BotOwner

        execute(WordArg("Prefix")) {
            val prefix = it.args.first

            prefixService.setPrefix(prefix)
            persistenceService.save(configuration)

            it.respond("Prefix set to: $prefix")
        }
    }

    command("setAdminRole") {
        description = "Sets the Administrator role  "
        requiredPermissionLevel = Permission.BotOwner

        execute(RoleArg) {
            val (role) = it.args
            configuration.getGuildConfig(it.guild!!.id)?.adminRole = role.name
            persistenceService.save(configuration)
            return@execute it.respond("Administrator role set to \"${role.name}\"")
        }
    }

    command("setStaffRole") {
        description = "Sets the Staff role"
        requiredPermissionLevel = Permission.BotOwner

        execute(RoleArg) {
            val (role) = it.args
            configuration.getGuildConfig(it.guild!!.id)?.staffRole = role.name
            persistenceService.save(configuration)
            return@execute it.respond("Staff role set to \"${role.name}\"")
        }
    }

    command("setModeratorRole") {
        description = "Sets the Moderator role"
        requiredPermissionLevel = Permission.BotOwner

        execute(RoleArg) {
            val (role) = it.args
            configuration.getGuildConfig(it.guild!!.id)?.moderatorRole = role.name
            persistenceService.save(configuration)
            return@execute it.respond("Moderator role set to \"${role.name}\"")
        }
    }
}

