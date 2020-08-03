package me.aberrantfox.judgebot.commands

import me.aberrantfox.judgebot.arguments.LowerMemberArg
import me.aberrantfox.judgebot.dataclasses.PunishmentType
import me.aberrantfox.judgebot.extensions.requiredPermissionLevel
import me.aberrantfox.judgebot.services.Permission
import me.aberrantfox.judgebot.services.RoleService
import me.aberrantfox.judgebot.services.RoleState
import me.jakejmattson.discordkt.api.annotations.CommandSet
import me.jakejmattson.discordkt.api.arguments.*
import me.jakejmattson.discordkt.api.dsl.command.commands
import me.jakejmattson.discordkt.api.extensions.jda.fullName
import kotlin.math.roundToLong

@CommandSet("Mutes")
fun createMuteCommands(roleService: RoleService) = commands {
    command("mute") {
        description = "Use this to mute a member"
        requiresGuild = true
        requiredPermissionLevel = Permission.Staff
        execute(LowerMemberArg, TimeArg, EveryArg) {
            val (targetMember, time, reason) = it.args
            roleService.applyRole(targetMember, time.roundToLong() * 1000, reason, PunishmentType.Mute)
            it.respond("User ${targetMember.fullName()} has been muted")
        }
    }

    command("gag") {
        description = "Temporarily mute a member for 5 minutes"
        requiresGuild = true
        requiredPermissionLevel = Permission.Staff
        execute(LowerMemberArg) {
            val targetMember = it.args.first
            roleService.applyRole(targetMember, 1000 * 60 * 5, "You've been muted temporarily so that a mod can handle something.", PunishmentType.Mute)
        }
    }

    command("unmute") {
        description = "Use this to mute a member"
        requiresGuild = true
        requiredPermissionLevel = Permission.Staff
        execute(LowerMemberArg) {
            val targetMember = it.args.first
            if (roleService.checkRoleState(targetMember, it.guild!!, PunishmentType.Mute) == RoleState.None)
                return@execute it.respond("User ${it.args.first.fullName()} isn't muted")
            roleService.removeRole(it.args.first, PunishmentType.Mute)
            it.respond("User ${it.args.first.fullName()} has been unmuted")
        }
    }

    command("blindfold") {
        description = "Use this to mute a member"
        requiresGuild = true
        requiredPermissionLevel = Permission.Moderator
        execute(LowerMemberArg, TimeArg, EveryArg) {
            val (targetMember, time, reason) = it.args
            roleService.applyRole(targetMember, time.roundToLong() * 1000, reason, PunishmentType.Blindfold)
            it.respond("User ${targetMember.fullName()} has been blindfolded")
        }
    }
    command("unblindfold") {
        description = "Use this to mute a member"
        requiresGuild = true
        requiredPermissionLevel = Permission.Moderator
        execute(LowerMemberArg) {
            val targetMember = it.args.first
            if (roleService.checkRoleState(targetMember, it.guild!!, PunishmentType.Blindfold) == RoleState.None)
                return@execute it.respond("User ${it.args.first.fullName()} isn't blindfolded")
            roleService.removeRole(it.args.first, PunishmentType.Blindfold)
            it.respond("User ${it.args.first.fullName()} has been unblindfolded")
        }
    }
}