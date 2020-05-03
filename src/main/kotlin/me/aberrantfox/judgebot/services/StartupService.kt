package me.aberrantfox.judgebot.services

import com.google.gson.Gson
import me.aberrantfox.judgebot.configuration.BotConfiguration
import me.aberrantfox.judgebot.utility.timeToString
import me.aberrantfox.judgebot.extensions.requiredPermissionLevel
import me.aberrantfox.kjdautils.api.annotation.Service
import me.aberrantfox.kjdautils.api.dsl.PrefixDeleteMode
import me.aberrantfox.kjdautils.discord.Discord
import me.aberrantfox.kjdautils.extensions.jda.fullName
import me.aberrantfox.kjdautils.extensions.jda.toMember
import java.awt.Color
import java.util.*

private val propFile = Properties::class.java.getResource("/properties.json").readText()
data class Properties(val author: String, val version: String, val kutils: String, val repository: String)
val project: Properties = Gson().fromJson(propFile, Properties::class.java)

@Service
class StartupService(configuration: BotConfiguration,
                     discord: Discord,
                     permissionsService: PermissionsService) {
    init {
        val startTime = Date()
        with(discord.configuration) {
            prefix = configuration.prefix
            deleteMode = PrefixDeleteMode.None
            allowMentionPrefix = true

            colors {
                infoColor = Color.CYAN
                failureColor = Color.RED
                successColor = Color.GREEN
            }

            mentionEmbed {
                    val self = it.guild.jda.selfUser
                    val requiredRole = configuration.getGuildConfig(it.guild.id)?.staffRole ?: "<Not Configured>"
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

            visibilityPredicate predicate@{
                it.guild ?: return@predicate false
                val member = it.user.toMember(it.guild!!)!!
                val permission = it.command.requiredPermissionLevel
                permissionsService.hasClearance(member, permission)
            }
        }
    }
}