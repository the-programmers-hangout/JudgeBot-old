package me.aberrantfox.judgebot.configuration

import me.aberrantfox.kjdautils.api.annotation.Data
import org.litote.kmongo.Id
import org.litote.kmongo.newId

@Data("config/config.json")
data class BotConfiguration(
        val owner: String = "insert-owner-id",
        val whitelist: List<String> = listOf("insert-valid-guild-ids"),
        val guilds: List<GuildConfiguration> = listOf(GuildConfiguration()),
        val dbConfiguration: DatabaseConfiguration = DatabaseConfiguration()
)

data class DatabaseConfiguration(
        val address: String = "localhost:27017"
)

data class GuildConfiguration(
    val id: String = "insert-id",
    val owner: String = "insert-owner-id"
)





