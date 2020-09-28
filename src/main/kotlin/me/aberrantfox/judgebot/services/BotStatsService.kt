package me.aberrantfox.judgebot.services

import me.aberrantfox.judgebot.utility.timeToString
import me.jakejmattson.discordkt.api.Discord
import me.jakejmattson.discordkt.api.annotations.Service
import me.jakejmattson.discordkt.api.dsl.command.Command

import java.util.*

@Service
class BotStatsService(private val discord: Discord) {
    private var startTime: Date = Date()

    val uptime: String
        get() = timeToString(Date().time - startTime.time)

    val ping: String
        get() = "${discord.jda.gatewayPing} ms"
}

