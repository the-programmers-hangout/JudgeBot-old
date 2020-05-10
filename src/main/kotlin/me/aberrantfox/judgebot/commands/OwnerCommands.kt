package me.aberrantfox.judgebot.commands

import me.aberrantfox.judgebot.configuration.BotConfiguration
import me.aberrantfox.judgebot.extensions.requiredPermissionLevel
import me.aberrantfox.judgebot.services.Permission
import me.aberrantfox.judgebot.services.PrefixService
import me.aberrantfox.kjdautils.api.annotation.CommandSet
import me.aberrantfox.kjdautils.api.dsl.command.commands
import me.aberrantfox.kjdautils.internal.arguments.*
import me.aberrantfox.kjdautils.internal.services.PersistenceService

@CommandSet("Owner")
fun createOwnerCommands(configuration: BotConfiguration, prefixService: PrefixService, persistenceService: PersistenceService) = commands {
    command("setPrefix") {
        description = "Set the bot's prefix."
        requiredPermissionLevel = Permission.BotOwner
        requiresGuild = true
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
        requiresGuild = true
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
        requiresGuild = true
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
        requiresGuild = true
        execute(RoleArg) {
            val (role) = it.args
            configuration.getGuildConfig(it.guild!!.id)?.moderatorRole = role.name
            persistenceService.save(configuration)
            return@execute it.respond("Moderator role set to \"${role.name}\"")
        }
    }

    command("setLogChannel") {
        requiredPermissionLevel = Permission.Administrator
        requiresGuild = true
        execute(TextChannelArg) {
            val channel = it.args.first

            val config = configuration.getGuildConfig(it.guild!!.id)
            config?.loggingConfiguration?.loggingChannel = channel.id
            persistenceService.save(configuration)

            it.respond("Logging channel set to **${channel.name}**")
        }
    }

    command("toggleLog") {
        requiredPermissionLevel = Permission.Administrator
        requiresGuild = true
        execute(ChoiceArg("LogToggle", "Role", "Infraction", "Punishment"),
                BooleanArg("On/Off", "On", "Off")) {
            val (log, toggle) = it.args
            val config = configuration.getGuildConfig(it.guild!!.id)

            when(log.toLowerCase()) {
                "role" -> config?.loggingConfiguration?.logRoles = toggle
                "infraction" -> config?.loggingConfiguration?.logInfractions = toggle
                "punishment" -> config?.loggingConfiguration?.logPunishments = toggle
            }
            persistenceService.save(configuration)
            it.respond("**$log** logging has been turned **${if(toggle) "On" else "Off"}**")
        }
    }
}

