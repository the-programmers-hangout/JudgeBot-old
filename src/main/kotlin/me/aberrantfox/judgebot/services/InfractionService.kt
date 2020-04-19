package me.aberrantfox.judgebot.services

import me.aberrantfox.judgebot.services.database.dataclasses.GuildMember
import me.aberrantfox.judgebot.services.database.dataclasses.Infraction
import me.aberrantfox.judgebot.services.database.dataclasses.infractionMap
import me.aberrantfox.kjdautils.api.annotation.Service
import net.dv8tion.jda.api.entities.User
import org.joda.time.DateTime
import org.joda.time.Days

@Service
class InfractionService(private val databaseService: DatabaseService, private val userService: UserService, private val ruleService: RuleService) {
    private val infractionCollection = databaseService.db.getCollection("infractionCollection")

    fun infract(target: User, userRecord: GuildMember, infraction: Infraction): GuildMember {
        userRecord.addInfraction(infraction)
        userRecord.points += calculateInfractionPoints(userRecord, infraction)

        return userService.updateUserRecord(userRecord)
    }

    // TODO: apply punishments for infraction to user, send DM etc...
    private fun calculateInfractionPoints(userRecord: GuildMember, infraction: Infraction): Int {
        val rule = ruleService.getRule(infraction.ruleBroken!!, infraction.guildId)
        val daysSinceLastInfraction = Days.daysBetween(DateTime(userRecord.lastInfraction).toLocalDate(), DateTime().toLocalDate()).days

        var dayTotal = daysSinceLastInfraction / 30
        if(dayTotal > 12) dayTotal = 12

        var points = (rule!!.weight * infractionMap[infraction.weight]!!) - dayTotal
        if (points < 0) points = 0

        return points
    }

}