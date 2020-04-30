package me.aberrantfox.judgebot.listeners

import com.google.common.eventbus.Subscribe
import me.aberrantfox.judgebot.services.RoleService
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.events.channel.text.TextChannelCreateEvent

class NewChannelOverrideListener(private val roleService: RoleService) {
    @Subscribe
    fun onTextChannelCreate(event: TextChannelCreateEvent) {
        event.channel.createPermissionOverride(roleService.getMutedRole(event.guild))
                .setDeny(Permission.MESSAGE_WRITE).queue()
        event.channel.createPermissionOverride(roleService.getBlindfoldRole(event.guild))
                .setDeny(Permission.MESSAGE_WRITE, Permission.VIEW_CHANNEL, Permission.MESSAGE_READ).queue()
    }
}
