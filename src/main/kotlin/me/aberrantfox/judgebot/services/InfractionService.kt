package me.aberrantfox.judgebot.services

import me.aberrantfox.judgebot.services.database.dataclasses.GuildMember
import me.aberrantfox.judgebot.services.database.dataclasses.Infraction
import me.aberrantfox.kjdautils.api.annotation.Service
import net.dv8tion.jda.api.entities.User

@Service
class InfractionService(private val databaseService: DatabaseService, private val userService: UserService) {
    private val infractionCollection = databaseService.db.getCollection("infractionCollection")

    fun infract(target: User, userRecord: GuildMember, infraction: Infraction): GuildMember {
        userRecord.addInfraction(infraction)
        return userService.updateUserRecord(userRecord)
    }

    // TODO: Points algorithm, apply punishments for infraction to user, etc...

}