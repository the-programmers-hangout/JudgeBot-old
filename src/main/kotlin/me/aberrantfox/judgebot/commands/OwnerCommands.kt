package me.aberrantfox.judgebot.commands

import me.aberrantfox.judgebot.configuration.Configuration
import me.aberrantfox.judgebot.extensions.requiredPermissionLevel
import me.aberrantfox.judgebot.services.Permission
import me.jakejmattson.discordkt.api.annotations.CommandSet
import me.jakejmattson.discordkt.api.arguments.*
import me.jakejmattson.discordkt.api.dsl.command.commands

@CommandSet("Owner")
fun createOwnerCommands(configuration: Configuration) = commands {
    command("setPrefix") {
        description = "Set the bot's prefix."
        requiredPermissionLevel = Permission.BotOwner
        requiresGuild = true
        execute(AnyArg("Prefix")) {
            val prefix = it.args.first

            configuration.getGuildConfig(it.guild!!.id)?.prefix = prefix
            configuration.save()

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
            configuration.save()
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
            configuration.save()
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
            configuration.save()
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
            configuration.save()

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
            configuration.save()
            it.respond("**$log** logging has been turned **${if(toggle) "On" else "Off"}**")
        }
    }
}

