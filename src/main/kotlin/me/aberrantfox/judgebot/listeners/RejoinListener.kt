package me.aberrantfox.judgebot.listeners

import com.google.common.eventbus.Subscribe
import me.aberrantfox.judgebot.services.BlindfoldService
import me.aberrantfox.judgebot.services.MuteService
import me.aberrantfox.judgebot.services.RoleService
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent

class RejoinListener(private val roleService: RoleService,
                     private val muteService: MuteService,
                     private val blindfoldService: BlindfoldService) {
    @Subscribe
    fun onGuildMemberJoinEvent(event: GuildMemberJoinEvent) {
        val member = event.member
        val guild = event.guild
        if (muteService.checkMuteState(member) == RoleService.RoleState.Tracked) {
            guild.addRoleToMember(member, roleService.getMutedRole(guild)).queue()
        }
        if (blindfoldService.checkBlindfoldState(member) == RoleService.RoleState.Tracked) {
            guild.addRoleToMember(member, roleService.getBlindfoldRole(guild)).queue()
        }
    }
}
