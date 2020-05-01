package me.aberrantfox.judgebot.services

import com.google.gson.Gson
import me.aberrantfox.judgebot.configuration.BotConfiguration
import me.aberrantfox.judgebot.extensions.requiredPermissionLevel
import me.aberrantfox.kjdautils.api.annotation.Service
import me.aberrantfox.kjdautils.api.dsl.PrefixDeleteMode
import me.aberrantfox.kjdautils.api.dsl.command.Command
import me.aberrantfox.kjdautils.api.dsl.embed
import me.aberrantfox.kjdautils.discord.Discord
import me.aberrantfox.kjdautils.extensions.jda.fullName
import me.aberrantfox.kjdautils.extensions.jda.toMember
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.MessageChannel
import net.dv8tion.jda.api.entities.User
import java.awt.Color

data class Properties(val author: String, val version: String, val kutils: String, val repository: String)

private val propFile = Properties::class.java.getResource("/properties.json").readText()
val project: Properties = Gson().fromJson(propFile, Properties::class.java)

@Service
class StartupService(configuration: BotConfiguration,
                     discord: Discord, permissionsService: PermissionsService) {
    init {
        with(discord.configuration) {
            prefix = configuration.prefix
            deleteMode = PrefixDeleteMode.None
            globalPath = "me.aberrantfox.judgebot."

            mentionEmbed = {
                embed {
                    val self = it.guild.jda.selfUser
                    val requiredRole = configuration.getGuildConfig(it.guild.id)?.staffRole ?: "<Not Configured>"

                    color = Color.MAGENTA
                    thumbnail = self.effectiveAvatarUrl
                    addField(self.fullName(), "A bot for managing discord infractions in an intelligent and user-friendly way.")
                    addInlineField("Required role", requiredRole)
                    addInlineField("Prefix", configuration.prefix)

                    with(project) {
                        val kotlinVersion = KotlinVersion.CURRENT

                        addField("Build Info", "```" +
                                "Version: $version\n" +
                                "KUtils: $kutils\n" +
                                "Kotlin: $kotlinVersion" +
                                "```")

                        addInlineField("Source", repository)
                    }
                }
            }

            visibilityPredicate = predicate@{ command: Command, user: User, _: MessageChannel, guild: Guild? ->
                guild ?: return@predicate false

                val member = user.toMember(guild)!!
                val permission = command.requiredPermissionLevel

                permissionsService.hasClearance(member, permission)
            }
        }
    }
}