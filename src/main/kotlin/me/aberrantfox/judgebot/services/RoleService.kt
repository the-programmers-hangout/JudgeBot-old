package me.aberrantfox.judgebot.services

import kotlinx.coroutines.Job
import me.aberrantfox.judgebot.configuration.BotConfiguration
import me.aberrantfox.judgebot.dataclasses.Punishment
import me.aberrantfox.judgebot.dataclasses.PunishmentType
import me.aberrantfox.judgebot.utility.applyRoleWithTimer
import me.aberrantfox.judgebot.utility.buildInfractionEmbed
import me.aberrantfox.kjdautils.api.annotation.Service
import me.aberrantfox.kjdautils.discord.Discord
import me.aberrantfox.kjdautils.extensions.jda.getRoleByName
import me.aberrantfox.kjdautils.extensions.jda.sendPrivateMessage
import me.aberrantfox.kjdautils.extensions.stdlib.toTimeString
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.Member
import org.joda.time.DateTime

typealias GuildID = String
typealias MuteRoleID = String
typealias UserId = String

enum class RoleState {
    None,
    Tracked,
    Untracked,
}

@Service
class RoleService(val configuration: BotConfiguration,
                  private val discord: Discord,
                  private val databaseService: DatabaseService) {

    private val blindfoldMap = hashMapOf<GuildID, MuteRoleID>()
    private val muteMap = hashMapOf<GuildID, MuteRoleID>()
    private val punishmentTimerMap = hashMapOf<Pair<GuildID, UserId>, Job>()
    private fun toKey(member: Member) = member.guild.id to member.user.id

    init {
        discord.jda.guilds.forEach { setupRoles(it) }
    }

    fun getRole(guild: Guild, type: PunishmentType) =
            if (type == PunishmentType.Mute) discord.jda.getRoleById(muteMap[guild.id]!!)!!
            else discord.jda.getRoleById(blindfoldMap[guild.id]!!)!!

    fun applyRole(member: Member, time: Long, reason: String, type: PunishmentType) {
        val guild = member.guild
        val user = member.user
        val key = toKey(member)
        val clearTime = DateTime.now().plus(time).millis

        if (key in punishmentTimerMap) {
            punishmentTimerMap[key]?.cancel()
            punishmentTimerMap.remove(key)
            databaseService.punishments.removePunishment(member, guild, type)
        }
        val punishment = Punishment(user.id, guild.id, type, clearTime, reason)
        databaseService.punishments.addPunishment(punishment)
        member.user.sendPrivateMessage(buildInfractionEmbed(member.asMention, time.toTimeString(), reason, type))
        punishmentTimerMap[toKey(member)] = applyRoleWithTimer(member, getRole(guild, type)!!, time) {
            removeRole(member, type)
        }
    }

    fun removeRole(member: Member, type: PunishmentType) {
        val user = member.user
        val guild = member.guild
        if (user.mutualGuilds.isNotEmpty()) {
            guild.removeRoleFromMember(member, getRole(guild, type)!!).queue()
        }
        databaseService.punishments.removePunishment(member, guild, type)
        punishmentTimerMap.remove(toKey(member))
    }

    fun checkRoleState(member: Member, guild: Guild, type: PunishmentType) = when {
        databaseService.punishments.findByType(member, guild, type)
                != null -> RoleState.Tracked
        member.roles.contains(getRole(member.guild, type)) -> RoleState.Untracked
        else -> RoleState.None
    }

    private fun handleExistingRoles(guild: Guild) {
        databaseService.punishments.findByGuild(guild).forEach {
            val difference = it.clearTime - DateTime.now().millis
            val member = guild.getMemberById(it.userId)
            if (member != null) {
                applyRoleWithTimer(member, getRole(guild, it.type), difference) { _ ->
                    removeRole(member, it.type)
                }
            }
        }
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