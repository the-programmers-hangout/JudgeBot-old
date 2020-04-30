package me.aberrantfox.judgebot.services

import com.google.gson.Gson
import me.aberrantfox.judgebot.configuration.BotConfiguration
import me.aberrantfox.judgebot.utility.timeToString
import me.aberrantfox.kjdautils.api.annotation.Service
import me.aberrantfox.kjdautils.api.dsl.PrefixDeleteMode
import me.aberrantfox.kjdautils.api.dsl.embed
import me.aberrantfox.kjdautils.discord.Discord
import me.aberrantfox.kjdautils.extensions.jda.fullName
import java.awt.Color
import java.util.*

data class Properties(val author: String, val version: String, val kutils: String, val repository: String)

private val propFile = Properties::class.java.getResource("/properties.json").readText()
val project: Properties = Gson().fromJson(propFile, Properties::class.java)

@Service
class StartupService(configuration: BotConfiguration, discord: Discord) {
    init {
        val startTime = Date()

        with(discord.configuration) {
            prefix = configuration.prefix
            deleteMode = PrefixDeleteMode.None
            globalPath = "me.aberrantfox.judgebot."

            mentionEmbed = {
                embed {
                    val self = it.guild.jda.selfUser
                    val requiredRole = configuration.getGuildConfig(it.guild.id)?.requiredRole ?: "<Not Configured>"
                    val milliseconds = Date().time - startTime.time

                    color = Color.MAGENTA
                    thumbnail = self.effectiveAvatarUrl
                    addField(self.fullName(), "A bot for managing discord infractions in an intelligent and user-friendly way.")
                    addInlineField("Required role", requiredRole)
                    addInlineField("Prefix", configuration.prefix)

                    with(project) {
                        val kotlinVersion = KotlinVersion.CURRENT

                        addField("Bot Info", "```" +
                                "Version: $version\n" +
                                "KUtils: $kutils\n" +
                                "Kotlin: $kotlinVersion\n" +
                                "Ping: ${discord.jda.gatewayPing}ms\n" +
                                "Uptime: ${timeToString(milliseconds)}" +
                                "```")

                        addInlineField("Source", repository)
                    }
                }
            }
        }
    }
}