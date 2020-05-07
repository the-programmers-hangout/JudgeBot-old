package me.aberrantfox.judgebot.services

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import me.aberrantfox.judgebot.dataclasses.PunishmentConfig
import me.aberrantfox.judgebot.dataclasses.PunishmentType
import me.aberrantfox.judgebot.utility.buildBadPfpEmbed
import me.aberrantfox.judgebot.utility.timeToString
import me.aberrantfox.kjdautils.api.annotation.Service
import me.aberrantfox.kjdautils.discord.Discord
import me.aberrantfox.kjdautils.extensions.jda.sendPrivateMessage
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.Member

@Service
class BadPfpService(private val roleService: RoleService,
                    private val discord: Discord) {
    private val badPfpTracker = hashMapOf<Pair<GuildID, UserId>, Job>()
    private fun toKey(member: Member) = member.guild.id to member.user.id

    fun applyBadPfp(target: Member, punishmentConfig: PunishmentConfig, guild: Guild) {
        target.user.sendPrivateMessage(buildBadPfpEmbed(target.asMention, timeToString(punishmentConfig.time!!)))
        roleService.applyRole(target, punishmentConfig.time!!, "Bad Pfp Mute", PunishmentType.Mute)
        badPfpTracker[toKey(target)] = GlobalScope.launch {
            delay(punishmentConfig.time!!)
            if(target.user.effectiveAvatarUrl == discord.jda.retrieveUserById(target.id).complete().effectiveAvatarUrl) {
                GlobalScope.launch {
                    delay(10000)
                    guild.ban(target, 1, "Having a bad profile picture and refusing to change it.").queue()
                }
            } else {
                target.user.sendPrivateMessage("Thank you for changing your avatar. You will not be banned.")
            }
        }
    }

    fun hasBadPfp(target: Member): Boolean {
        return badPfpTracker.containsKey(toKey(target))
    }

    fun cancelBadPfp(target: Member) {
        val key = toKey(target)
        if (badPfpTracker.containsKey(key)) {
            badPfpTracker[key]?.cancel()
            badPfpTracker.remove(key)
            roleService.removeRole(target, PunishmentType.Mute)
        }
    }
}