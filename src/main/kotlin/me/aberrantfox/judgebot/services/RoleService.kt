package me.aberrantfox.judgebot.services

import me.aberrantfox.judgebot.configuration.BotConfiguration
import me.aberrantfox.kjdautils.api.annotation.Service
import me.aberrantfox.kjdautils.discord.Discord
import me.aberrantfox.kjdautils.extensions.jda.getRoleByName
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.Guild

typealias GuildID = String
typealias MuteRoleID = String
typealias UserId = String

@Service
class RoleService(val configuration: BotConfiguration,
                  private val discord: Discord) {
    private val blindfoldMap = hashMapOf<GuildID, MuteRoleID>()
    private val muteMap = hashMapOf<GuildID, MuteRoleID>()

    init {
        discord.jda.guilds.forEach { setupRoles(it) }
    }

    fun getMutedRole(guild: Guild) = discord.jda.getRoleById(muteMap[guild.id]!!)!!
    fun getBlindfoldRole(guild: Guild) = discord.jda.getRoleById(blindfoldMap[guild.id]!!)!!

    enum class RoleState {
        None,
        Tracked,
        Untracked,
    }

    private fun setupRoles(guild: Guild) {
        val blindfoldRoleName = configuration.getGuildConfig(guild.id)!!.security.blindfoldRole
        val mutedRoleName = configuration.getGuildConfig(guild.id)!!.security.mutedRole
        val possibleMutedRole = guild.getRoleByName(mutedRoleName, true)
        val possibleBlindfoldRole = guild.getRoleByName(blindfoldRoleName, true)
        val blindfoldRole = possibleBlindfoldRole ?: guild.createRole().setName(blindfoldRoleName).complete()
        val mutedRole = possibleMutedRole ?: guild.createRole().setName(mutedRoleName).complete()

        blindfoldMap[guild.id] = blindfoldRole.id
        muteMap[guild.id] = mutedRole.id

        guild.textChannels
                .filter {
                    it.rolePermissionOverrides.none { override ->
                        override.role == blindfoldRole
                    }
                }
                .forEach {
                    it.putPermissionOverride(blindfoldRole)
                            .setDeny(Permission.MESSAGE_WRITE, Permission.VIEW_CHANNEL, Permission.MESSAGE_READ).queue()
                }
        guild.textChannels
                .filter {
                    it.rolePermissionOverrides.none { override ->
                        override.role == mutedRole
                    }
                }
                .forEach {
                    it.createPermissionOverride(mutedRole).setDeny(Permission.MESSAGE_WRITE, Permission.MESSAGE_ADD_REACTION).queue()
                }
    }
}