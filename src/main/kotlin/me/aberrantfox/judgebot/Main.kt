package me.aberrantfox.judgebot

import com.google.gson.Gson
import me.aberrantfox.judgebot.configuration.Configuration
import me.aberrantfox.judgebot.extensions.requiredPermissionLevel
import me.aberrantfox.judgebot.services.BotStatsService
import me.aberrantfox.judgebot.services.PermissionsService
import me.jakejmattson.discordkt.api.dsl.bot
import me.jakejmattson.discordkt.api.extensions.jda.fullName
import me.jakejmattson.discordkt.api.extensions.jda.toMember
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.requests.GatewayIntent
import net.dv8tion.jda.api.utils.ChunkingFilter
import net.dv8tion.jda.api.utils.MemberCachePolicy
import java.awt.Color
import java.util.*
import kotlin.system.exitProcess

data class Properties(val author: String, val version: String, val discordKt: String, val repository: String)

private val propFile = Properties::class.java.getResource("/properties.json").readText()
val project = Gson().fromJson(propFile, Properties::class.java)

fun main(args: Array<String>) {
    val token = args.firstOrNull()

    if(token == null || token == "UNSET") {
        println("Please specify bot_Token ")
        exitProcess(-1)
    }

    bot(token) {
        client { token ->
            JDABuilder.createDefault(token)
                    .setChunkingFilter(ChunkingFilter.ALL)
                    .enableIntents(EnumSet.allOf(GatewayIntent::class.java))
                    .setMemberCachePolicy(MemberCachePolicy.ALL)
        }

        configure {
            allowMentionPrefix = true
            val (configuration, permissionsService, botStatsService)
                    = it.getInjectionObjects(Configuration::class, PermissionsService::class, BotStatsService::class)

            prefix {
                it.guild?.let {
                    configuration.getGuildConfig(it.id)?.prefix.takeUnless { it.isNullOrBlank() } ?: "judge!"
                } ?: "<none>"
            }

            colors {
                infoColor = Color.CYAN
                failureColor = Color.RED
                successColor = Color.GREEN
            }

            mentionEmbed {
                val channel = it.channel
                val self = channel.jda.selfUser
                val requiredRole = configuration.getGuildConfig(it.guild!!.id)?.staffRole ?: "<Not Configured>"

                color = Color.MAGENTA
                thumbnail = self.effectiveAvatarUrl
                addField(self.fullName(), "A bot for managing discord infractions in an intelligent and user-friendly way.")
                addInlineField("Required role", requiredRole)
                addInlineField("Prefix", configuration.prefix)

                with(project) {
                    val kotlinVersion = KotlinVersion.CURRENT

                    addField("Bot Info", "```" +
                            "Version: $version\n" +
                            "DiscordKt: $discordKt\n" +
                            "Kotlin: $kotlinVersion\n" +
                            "Ping: ${botStatsService.ping}\n" +
                            "Uptime: ${botStatsService.uptime}" +
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
