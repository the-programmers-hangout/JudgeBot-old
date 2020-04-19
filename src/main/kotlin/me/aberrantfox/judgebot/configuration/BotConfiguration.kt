package me.aberrantfox.judgebot.configuration

import me.aberrantfox.kjdautils.api.annotation.Data

@Data("config/config.json")
data class BotConfiguration(
        val owner: String = "insert-owner-id",
        val whitelist: List<String> = listOf("insert-valid-guild-ids"),
        val guilds: List<GuildConfiguration> = listOf(GuildConfiguration()),
        val dbConfiguration: DatabaseConfiguration = DatabaseConfiguration(),
        val security: Security = Security()
)

data class DatabaseConfiguration(
        val address: String = "localhost:27017",
        val databaseName: String = "judgebot"
)

data class GuildConfiguration(
        val id: String = "insert-id",
        val owner: String = "insert-owner-id"
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




