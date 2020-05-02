package me.aberrantfox.judgebot.services

import me.aberrantfox.judgebot.configuration.BotConfiguration
import me.aberrantfox.judgebot.dataclasses.*
import me.aberrantfox.judgebot.utility.getEmbedColor
import me.aberrantfox.kjdautils.api.annotation.Service
import me.aberrantfox.kjdautils.api.dsl.embed
import me.aberrantfox.kjdautils.extensions.jda.getMemberJoinString
import me.aberrantfox.kjdautils.extensions.stdlib.formatJdaDate
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.MessageEmbed
import org.litote.kmongo.eq
import org.litote.kmongo.findOne
import org.litote.kmongo.getCollection
import org.litote.kmongo.updateOne

@Service
class UserService(private val databaseService: DatabaseService, private val ruleService: RuleService, private val config: BotConfiguration) {
    private val userCollection = databaseService.db.getCollection<GuildMember>("userCollection")

    fun getOrCreateUserRecord(target: Member, guildId: String): GuildMember {
        var userRecord = userCollection.findOne(GuildMember::userId eq target.id)
        return if(userRecord != null) {
            userRecord.ensureGuildDetailsPresent(guildId)
            userRecord
        } else {
            val guildMember = GuildMember(target.id)
            guildMember.guilds.add(GuildDetails(guildId))
            userCollection.insertOne(guildMember)
            guildMember
        }
    }

    fun getUserHistory(target: Member, userRecord: GuildMember, guild: Guild, incrementHistoryCount: Boolean): MessageEmbed {
        if(incrementHistoryCount) {
            this.incrementUserHistory(userRecord, guild.id)
        }
        return userStatusEmbed(target, userRecord, guild,true)
    }

    fun updateUserRecord(user: GuildMember): GuildMember {
        this.userCollection.updateOne(GuildMember::userId eq user.userId, user)
        return user
    }

    private fun incrementUserHistory(user: GuildMember, guildId: String): GuildMember {
        user.incrementHistoryCount(guildId)
        this.updateUserRecord(user)
        return user
    }

    private fun userStatusEmbed(target: Member, member: GuildMember, guild: Guild, includeModerator: Boolean) =
            embed {
                val guildDetails = member.guilds.find{ it.guildId == guild.id }!!
                val (notes, infractions) = guildDetails.infractions.partition { it.weight == InfractionWeight.Note }

                author {
                    name = "${target.user.asTag}'s Record"
                    iconUrl = target.user.effectiveAvatarUrl
                }
                color = getEmbedColor(member.getStatus(guild.id, config))

                addInlineField("Notes", "${notes.filter{ it.guildId == guild.id }.size}")
                addInlineField("Infractions", "${infractions.filter{ it.guildId == guild.id }.size}")
                addInlineField("Status","${member.getStatus(guild.id, config)}")
                addInlineField("Join date", "${guild.getMemberJoinString(target.user)}")
                addInlineField("Creation date", "${target.timeCreated.toString().formatJdaDate()}")
                addInlineField("History Invokes","${guildDetails.historyCount}")

                field {
                    name = ""
                    value = "**__Rule Summary:__**"
                }

                val rules = ruleService.getRules(guild.id)
                val rulesBroken = groupRulesBroken(member, guild.id, rules)
                rules.chunked(2).forEachIndexed{
                    index, chunkedRules ->
                    chunkedRules.forEachIndexed {
                        index, it ->
                        if (index == 0) {
                            field {
                                name = "${it.number}: ${it.title}"
                                value = "Weight: **${it.weight}** :: Broken: **${rulesBroken[it.number] ?: 0}** \u2800\u2800"
                                inline = true
                            }
                        } else {
                            field {
                                name = "${it.number}: ${it.title}"
                                value = "Weight: **${it.weight}** :: Broken: **${rulesBroken[it.number] ?: 0}**"
                                inline = true
                            }
                        }
                    }
                    if(index != rules.chunked(2).size - 1) addBlankField(false)
                }
            }

    private fun groupRulesBroken(user: GuildMember, guildId: String, rules: MutableList<Rule>): Map<Int?, Int> {
        return user.getGuildInfo(guildId)!!.infractions
                .filter { it.guildId == guildId }
                .groupBy { it.ruleBroken }
                .mapValues { it.value.size }
    }
}
