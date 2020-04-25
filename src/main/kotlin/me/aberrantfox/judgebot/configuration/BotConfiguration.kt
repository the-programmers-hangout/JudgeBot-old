package me.aberrantfox.judgebot.configuration

import me.aberrantfox.kjdautils.api.annotation.Data
import net.dv8tion.jda.api.entities.MessageEmbed

@Data("config/config.json")
data class BotConfiguration(
        val owner: String = "insert-owner-id",
        val whitelist: List<String> = listOf("insert-valid-guild-ids"),
        val guilds: List<GuildConfiguration> = listOf(GuildConfiguration()),
        val dbConfiguration: DatabaseConfiguration = DatabaseConfiguration(),
        var prefix: String = "judge!"
) {
        fun getGuildConfig(guildId: String) = guilds.firstOrNull { it.id == guildId }
}

data class DatabaseConfiguration(
        val address: String = "localhost:27017",
        val databaseName: String = "judgebot"
)

data class GuildConfiguration(
        val id: String = "insert-id",
        val owner: String = "insert-owner-id",
        val embedThumbnail: String = "",
        var staffRole: String = "",
        var adminRole: String = "",
        val security: Security = Security()
)

data class Security(
        val pointsToStatusMap: HashMap<Int, String> = hashMapOf(
                0 to "Clear",
                10 to "Green",
                20 to "Yellow",
                30 to "Orange",
                40 to "Red"
        ),
        val pointsMax: Int = 50
)




