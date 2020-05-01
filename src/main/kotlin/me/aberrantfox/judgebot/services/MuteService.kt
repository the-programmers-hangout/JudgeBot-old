package me.aberrantfox.judgebot.services

import kotlinx.coroutines.*
import me.aberrantfox.judgebot.configuration.BotConfiguration
import me.aberrantfox.judgebot.dataclasses.Punishment
import me.aberrantfox.judgebot.dataclasses.PunishmentType
import me.aberrantfox.judgebot.utility.buildMuteEmbed
import me.aberrantfox.kjdautils.api.annotation.Service
import me.aberrantfox.kjdautils.discord.Discord
import me.aberrantfox.kjdautils.extensions.jda.sendPrivateMessage
import me.aberrantfox.kjdautils.extensions.stdlib.toTimeString
import net.dv8tion.jda.api.entities.Member
import org.joda.time.DateTime
import org.litote.kmongo.and
import org.litote.kmongo.eq
import org.litote.kmongo.findOne
import org.litote.kmongo.getCollection

@Service
class MuteService(private val configuration: BotConfiguration,
                  private val discord: Discord,
                  private val databaseService: DatabaseService,
                  private val roleService: RoleService) {
    private val muteCollection = databaseService.db.getCollection<Punishment>("punishmentCollection")
    private val unmuteTimerTaskMap = hashMapOf<Pair<GuildID, UserId>, Job>()
    private fun toKey(member: Member) = member.guild.id to member.user.id

    init {
        handleExistingMutes()
    }

    fun mute(member: Member, time: Long, reason: String) {
        val guild = member.guild
        val user = member.user
        val key = toKey(member)
        val unmuteTime = DateTime.now().plus(time).millis

        if (key in unmuteTimerTaskMap) {
            unmuteTimerTaskMap[key]?.cancel()
            unmuteTimerTaskMap.remove(key)
            muteCollection.deleteOne(Punishment::userId eq member.id)
        }
        muteCollection.insertOne(Punishment(user.id, guild.id, PunishmentType.Mute, unmuteTime, reason))
        guild.addRoleToMember(member, roleService.getMutedRole(guild)).queue()
        member.user.sendPrivateMessage(buildMuteEmbed(member.asMention, time.toTimeString(), reason))
        unmuteTimerTaskMap[toKey(member)] = scheduleUnmute(member, time)
    }

    fun unmute(member: Member) {
        val user = member.user
        val guild = member.guild
        if(user.mutualGuilds.isNotEmpty()) {
            guild.removeRoleFromMember(member, roleService.getMutedRole(guild)).queue()
        }
        muteCollection.deleteOne(and(Punishment::userId eq member.id, Punishment::guildId eq guild.id))
        unmuteTimerTaskMap.remove(toKey(member))
    }

    fun checkMuteState(member: Member) = when {
        muteCollection.findOne(and(Punishment::userId eq member.id), Punishment::type eq PunishmentType.Mute)
                != null -> RoleService.RoleState.Tracked
        member.roles.contains(roleService.getMutedRole(member.guild)) -> RoleService.RoleState.Untracked
        else -> RoleService.RoleState.None
    }

    private fun handleExistingMutes() {
        muteCollection.find().forEach {
            val difference = it.clearTime - DateTime.now().millis
            val guild = discord.jda.getGuildById(it.guildId)

            val member = guild?.getMemberById(it.userId)
            if (member != null) {
                scheduleUnmute(member, difference)
            }
        }
    }

    private fun scheduleUnmute(member: Member, time: Long) = GlobalScope.launch {
        if (time <= 0) {
            unmute(member)
            return@launch
        }
        delay(time)
        unmute(member)
    }
}
