package me.aberrantfox.judgebot.services

import me.aberrantfox.judgebot.configuration.BotConfiguration
import me.aberrantfox.judgebot.services.database.dataclasses.GuildMember
import me.aberrantfox.judgebot.services.database.dataclasses.Infraction
import me.aberrantfox.judgebot.services.database.dataclasses.InfractionWeight
import me.aberrantfox.judgebot.services.database.dataclasses.Rule
import me.aberrantfox.judgebot.utility.getEmbedColor
import me.aberrantfox.kjdautils.api.annotation.Service
import me.aberrantfox.kjdautils.api.dsl.embed
import me.aberrantfox.kjdautils.extensions.jda.fullName
import me.aberrantfox.kjdautils.extensions.jda.getMemberJoinString
import me.aberrantfox.kjdautils.extensions.stdlib.formatJdaDate
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.entities.User
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import org.litote.kmongo.eq
import org.litote.kmongo.findOne
import org.litote.kmongo.getCollection
import org.litote.kmongo.updateOne
import java.awt.Color

@Service
class UserService(private val databaseService: DatabaseService, private val ruleService: RuleService, private val config: BotConfiguration) {
    private val userCollection = databaseService.db.getCollection<GuildMember>("userCollection")

    fun getOrCreateUserRecord(target: User): GuildMember {
        var userRecord = userCollection.findOne(GuildMember::userId eq target.id)
        return if(userRecord != null)
            userRecord else
        {
            userCollection.insertOne(GuildMember(target.id))
            return GuildMember(target.id)
        }
    }

    fun getUserHistory(target: User, userRecord: GuildMember, guild: Guild, incrementHistoryCount: Boolean): MessageEmbed {
        if(incrementHistoryCount) {
            this.incrementUserHistory(userRecord)
        }
        return userStatusEmbed(target, userRecord, guild,true)
    }

    fun updateUserRecord(user: GuildMember): GuildMember {
        this.userCollection.updateOne(GuildMember::userId eq user.userId, user)
        return user
    }

    private fun incrementUserHistory(user: GuildMember): GuildMember {
        user.incrementHistoryCount()
        this.updateUserRecord(user)
        return user
    }

    private fun buildHistoryEmbed(target: User, member: GuildMember, guild: Guild, includeModerator: Boolean)  =
            embed {
                val (notes, infractions) = member.infractions.partition { it.weight == InfractionWeight.Note }

                title = "${target.fullName()}'s Record"
                thumbnail = target.effectiveAvatarUrl

                field {
                    value = "__**Summary**__"
                    inline = false
                }

                field {
                    name = "Information"
                    value = "Total notes: **${notes.size}**" +
                            "\nTotal infractions: **${infractions.size}**" +
                            "\nJoin date: **${guild.getMemberJoinString(target)}**" +
                            "\nCreation date: **${target.timeCreated.toString().formatJdaDate()}**"

                    if(includeModerator){
                        value +="\nHistory has been invoked **${member.historyCount}** times."
                    }
                }
                field {
                    value = "__**Infractions**__"
                    inline = false
                }

                infractions.forEach { infraction ->
                    field {
                        name = "Weight :: __${infraction.weight}__"
                        value = infraction.reason
                        inline = false

                        if(includeModerator) {
                            value += "\nIssued by **${infraction.moderator}** on **${DateTime(infraction.dateTime).toString(DateTimeFormat.forPattern("dd/MM/yyyy"))}**"
                        }
                    }

                    if(infraction.infractionNote != null) {
                        field {
                            name = "Moderator Note:"
                            value = infraction.infractionNote
                            inline = false
                        }
                    }
                }

                if (infractions.isEmpty()) {
                    field {
                        name = "No Infractions"
                        value = "Clean as a whistle, sir."
                        inline = false
                    }
                }

                field {
                    value = "__**Notes**__"
                    inline = false
                }

                notes.forEach { infraction ->
                    field {
                        name = "Weight :: __${infraction.weight}__"
                        value = infraction.reason
                        inline = false

                        if(includeModerator) {
                            value += "\nIssued by **${infraction.moderator}** on **${infraction.dateTime.toString()}**"
                        }
                    }
                }

                if (notes.isEmpty()) {
                    field {
                        name = "No Notes"
                        value = "User has no notes written."
                        inline = false
                    }
                }

            }

    private fun userStatusEmbed(target: User, member: GuildMember, guild: Guild, includeModerator: Boolean) =
            embed {
                val (notes, infractions) = member.infractions.partition { it.weight == InfractionWeight.Note }

                author {
                    name = "${target.asTag}'s Record"
                    iconUrl = target.effectiveAvatarUrl
                }
                color = getEmbedColor(member.getStatus(guild.id, config))

                addInlineField("Notes", "${notes.size}")
                addInlineField("Infractions", "${infractions.size}")
                addInlineField("Status","${member.getStatus(guild.id, config)}")
                addInlineField("Join date", "${guild.getMemberJoinString(target)}")
                addInlineField("Creation date", "${target.timeCreated.toString().formatJdaDate()}")
                addInlineField("History Invokes","${member.historyCount}")

                field {
                    name = ""
                    value = "**__Rule Summary:__**"
                }

                val rules = ruleService.getRules(guild.id)
                val rulesBroken = groupRulesBroken(member, rules)
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

    private fun groupRulesBroken(user: GuildMember, rules: MutableList<Rule>): Map<Int?, Int> {
        return user.infractions.groupBy { it.ruleBroken }.mapValues { it.value.size }
    }
}
