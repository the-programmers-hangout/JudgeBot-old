package me.aberrantfox.judgebot.listeners

import com.google.common.eventbus.Subscribe
import me.aberrantfox.judgebot.dataclasses.PunishmentType
import me.aberrantfox.judgebot.services.RoleService
import me.aberrantfox.judgebot.services.RoleState
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent

class RejoinListener(private val roleService: RoleService) {
    @Subscribe
    fun onGuildMemberJoinEvent(event: GuildMemberJoinEvent) {
        val member = event.member
        val guild = event.guild
        if (roleService.checkRoleState(member, guild, PunishmentType.Mute) == RoleState.Tracked) {
            guild.addRoleToMember(member, roleService.getRole(guild, PunishmentType.Mute)).queue()
        } else if (roleService.checkRoleState(member, guild, PunishmentType.Mute) == RoleState.Tracked) {
            guild.addRoleToMember(member, roleService.getRole(guild, PunishmentType.Blindfold)).queue()
        }
    }
}
