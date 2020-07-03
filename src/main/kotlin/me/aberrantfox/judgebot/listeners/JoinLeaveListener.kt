package me.aberrantfox.judgebot.listeners

import com.google.common.eventbus.Subscribe
import me.aberrantfox.judgebot.services.DatabaseService
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent
import org.joda.time.DateTime

class JoinLeaveListener(private val databaseService: DatabaseService) {
    @Subscribe
    fun onGuildMemberLeave(event: GuildMemberRemoveEvent) {
        val userRecord = databaseService.users.getOrCreateUser(event.member!!, event.guild.id)
        val leaveTime = DateTime.now().millis
        databaseService.users.insertGuildLeave(userRecord, event.guild, event.member!!.timeJoined.toEpochSecond(), leaveTime)
    }
}