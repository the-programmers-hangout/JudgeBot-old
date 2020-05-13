package me.aberrantfox.judgebot.listeners

import com.google.common.eventbus.Subscribe
import me.aberrantfox.judgebot.dataclasses.PunishmentType
import me.aberrantfox.judgebot.services.RoleService
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.events.channel.text.TextChannelCreateEvent

class NewChannelOverrideListener(private val roleService: RoleService) {
    @Subscribe
    fun onTextChannelCreate(event: TextChannelCreateEvent) {
        event.channel.createPermissionOverride(roleService.getRole(event.guild, PunishmentType.Mute))
                .setDeny(Permission.MESSAGE_WRITE, Permission.MESSAGE_ADD_REACTION).queue()
        event.channel.createPermissionOverride(roleService.getRole(event.guild, PunishmentType.Blindfold))
                .setDeny(Permission.MESSAGE_WRITE, Permission.VIEW_CHANNEL, Permission.MESSAGE_READ).queue()
    }
}
