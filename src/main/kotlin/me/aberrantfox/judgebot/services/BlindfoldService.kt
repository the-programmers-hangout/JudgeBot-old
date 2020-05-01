package me.aberrantfox.judgebot.services

import kotlinx.coroutines.*
import me.aberrantfox.judgebot.configuration.BotConfiguration
import me.aberrantfox.judgebot.dataclasses.Punishment
import me.aberrantfox.judgebot.dataclasses.PunishmentType
import me.aberrantfox.judgebot.utility.buildBlindfoldEmbed
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
class BlindfoldService(private val configuration: BotConfiguration,
                       private val discord: Discord,
                       private val databaseService: DatabaseService,
                       private val roleService: RoleService) {
    private val blindfoldCollection = databaseService.db.getCollection<Punishment>("punishmentCollection")
    private val blindfoldTimerMap = hashMapOf<Pair<GuildID, UserId>, Job>()
    private fun toKey(member: Member) = member.guild.id to member.user.id

   init {
       handleExistingBlindfolds()
   }

    fun blindfold(member: Member, time: Long, reason: String) {
        val guild = member.guild
        val user = member.user
        val key = toKey(member)
        val unblindfoldTime = DateTime.now().plus(time).millis

        if (key in blindfoldTimerMap) {
            blindfoldTimerMap[key]?.cancel()
            blindfoldTimerMap.remove(key)
            blindfoldCollection.deleteOne(Punishment::userId eq member.id)
        }
        blindfoldCollection.insertOne(Punishment(user.id, guild.id, PunishmentType.Blindfold, unblindfoldTime, reason))
        guild.addRoleToMember(member, roleService.getBlindfoldRole(guild)).queue()
        member.user.sendPrivateMessage(buildBlindfoldEmbed(member.asMention, time.toTimeString(), reason))
        blindfoldTimerMap[toKey(member)] = scheduleUnblindfold(member, time)
    }

    fun checkBlindfoldState(member: Member) = when {
        blindfoldCollection.findOne(and(Punishment::userId eq member.id, Punishment::type eq PunishmentType.Blindfold))
                != null -> RoleService.RoleState.Tracked
        member.roles.contains(roleService.getMutedRole(member.guild)) -> RoleService.RoleState.Untracked
        else -> RoleService.RoleState.None
    }

    fun unblindfold(member: Member) {
        val user = member.user
        val guild = member.guild
        if(user.mutualGuilds.isNotEmpty()) {
            guild.removeRoleFromMember(member, roleService.getBlindfoldRole(guild)).queue()
        }
        blindfoldCollection.deleteOne(org.litote.kmongo.and(Punishment::userId eq member.id, Punishment::guildId eq guild.id))
        blindfoldTimerMap.remove(toKey(member))
    }

    fun handleExistingBlindfolds() {
        blindfoldCollection.find().forEach {
            val difference = it.clearTime - DateTime.now().millis
            val guild = discord.jda.getGuildById(it.guildId)

            val member = guild?.getMemberById(it.userId)
            if (member != null) {
                scheduleUnblindfold(member, difference)
            }
        }
    }

    private fun scheduleUnblindfold(member: Member, time: Long) = GlobalScope.launch {
        if (time <= 0) {
            unblindfold(member)
            return@launch
        }
        delay(time)
        unblindfold(member)
    }
}