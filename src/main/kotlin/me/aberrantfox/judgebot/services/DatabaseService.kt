package me.aberrantfox.judgebot.services

import me.aberrantfox.judgebot.configuration.BotConfiguration
import me.aberrantfox.judgebot.services.database.PunishmentOperations
import me.aberrantfox.judgebot.services.database.RuleOperations
import me.aberrantfox.judgebot.services.database.UserOperations
import me.aberrantfox.kjdautils.api.annotation.Service

@Service
open class DatabaseService(val config: BotConfiguration,
                           val punishments: PunishmentOperations,
                           val users: UserOperations,
                           val rules: RuleOperations) {
}
