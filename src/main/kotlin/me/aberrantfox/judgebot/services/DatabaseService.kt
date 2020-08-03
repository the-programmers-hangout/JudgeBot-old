package me.aberrantfox.judgebot.services

import me.aberrantfox.judgebot.configuration.Configuration
import me.aberrantfox.judgebot.services.database.PunishmentOperations
import me.aberrantfox.judgebot.services.database.RuleOperations
import me.aberrantfox.judgebot.services.database.UserOperations
import me.jakejmattson.discordkt.api.annotations.Service

@Service
open class DatabaseService(val config: Configuration,
                           val punishments: PunishmentOperations,
                           val users: UserOperations,
                           val rules: RuleOperations) {
}
