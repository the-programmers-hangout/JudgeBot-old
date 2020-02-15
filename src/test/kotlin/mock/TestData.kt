package mock

import io.mockk.*
import me.aberrantfox.judgebot.configuration.BotConfiguration
import me.aberrantfox.judgebot.configuration.DatabaseConfiguration
import me.aberrantfox.judgebot.configuration.GuildConfiguration
import me.aberrantfox.judgebot.services.DatabaseService
import me.aberrantfox.judgebot.services.database.dataclasses.Rule
import me.aberrantfox.kjdautils.api.dsl.command.ArgumentContainer
import me.aberrantfox.kjdautils.api.dsl.command.CommandEvent
import me.aberrantfox.kjdautils.api.getInjectionObject
import me.aberrantfox.kjdautils.discord.Discord
import net.dv8tion.jda.api.entities.Guild
import org.junit.jupiter.api.Test
import org.litote.kmongo.newId
import kotlin.test.assertEquals

fun guildMock() = mockk<Guild>(relaxed = true)  {
    every { id } returns "test-guild"
}

fun discordMock() = mockk<Discord>(relaxed = true)

fun commandEventMock() = mockk<CommandEvent<*>>(relaxed = true) {
    every { discord } returns discordMock()
    every { guild } returns guildMock()
}

object TestData {
    object Database {
        val dbTestConfiguration: DatabaseConfiguration = DatabaseConfiguration(databaseName = "test")
    }

    val testRules: List<Rule> = listOf(
            Rule(newId(), "test-guild", 1, "testRule1", "testTitle1", "testDescription1", 1),
            Rule(newId(), "test-guild", 2, "testRule2", "testTitle2", "testDescription2", 2),
            Rule(newId(), "test-guild", 3, "testRule3", "testTitle3", "testDescription3", 3)
    )

    val botTestConfiguration: BotConfiguration = BotConfiguration(
            owner = "test-owner",
            whitelist = listOf("testGuild"),
            guilds = listOf(GuildConfiguration("test-guild-id", "test-owner-id")),
            dbConfiguration = Database.dbTestConfiguration
    )

}

