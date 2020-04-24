package me.aberrantfox.judgebot.services

import me.aberrantfox.judgebot.configuration.BotConfiguration
import me.aberrantfox.kjdautils.api.annotation.Service
import net.dv8tion.jda.api.entities.Member

enum class Permission {
    BotOwner,
    GuildOwner,
    Administrator,
    Staff,
}

val DEFAULT_REQUIRED_PERMISSION = Permission.Staff

@Service
class PermissionsService(private val configuration: BotConfiguration) {
    fun hasClearance(member: Member, requiredPermissionLevel: Permission): Boolean {
        return member.getPermissionLevel().ordinal <= requiredPermissionLevel.ordinal
    }
    fun getPermissionLevel(member: Member) = member.getPermissionLevel().ordinal

    private fun Member.getPermissionLevel() =
            when {
                isBotOwner() -> Permission.BotOwner
                isGuildOwner() -> Permission.GuildOwner
                isAdministrator() -> Permission.Administrator
                isStaff() -> Permission.Staff
                else -> Permission.Everyone
            }

    private fun Member.isBotOwner() = user.id == configuration.getGuildConfig(guild.id)?.owner
    private fun Member.isGuildOwner() = isOwner
    private fun Member.isAdministrator() : Boolean {
        val guildConfig = configuration.getGuildConfig(guild.id)!!

        val requiredRole = guildConfig.adminRole?.let {
            guild.getRolesByName(it, true).firstOrNull()
        } ?: return false

        return requiredRole in roles
    }
    private fun Member.isStaff(): Boolean {
        val guildConfig = configuration.getGuildConfig(guild.id)!!

        val requiredRole = guildConfig.staffRole?.let {
            guild.getRolesByName(it, true).firstOrNull()
        } ?: return false

        return requiredRole in roles
    }
}