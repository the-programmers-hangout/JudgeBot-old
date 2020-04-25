package mock

import io.mockk.*
import me.aberrantfox.judgebot.configuration.BotConfiguration
import me.aberrantfox.judgebot.configuration.DatabaseConfiguration
import me.aberrantfox.judgebot.configuration.GuildConfiguration
import me.aberrantfox.judgebot.services.EmbedService
import me.aberrantfox.judgebot.services.RuleService
import me.aberrantfox.judgebot.dataclasses.Rule
import me.aberrantfox.kjdautils.api.dsl.command.ArgumentContainer
import me.aberrantfox.kjdautils.api.dsl.command.CommandEvent
import me.aberrantfox.kjdautils.discord.Discord
import me.aberrantfox.kjdautils.internal.services.ConversationService
import net.dv8tion.jda.api.entities.Guild
import org.litote.kmongo.newId

fun guildMock() = mockk<Guild>(relaxed = true) {
    every { id } returns "test-guild"
}

fun discordMock() = mockk<Discord>(relaxed = true)

fun commandEventMock() = mockk<CommandEvent<ArgumentContainer>>(relaxed = true) {
    every { discord } returns discordMock()
    every { guild } returns guildMock()
}

fun conversationServiceMock() = mockk<ConversationService>(relaxed = true)

fun embedServiceMock() = mockk<EmbedService>(relaxed = true)

fun databaseServiceMock() = mockk<RuleService>(relaxed = true) {
    every { getRule(1, "test-guild") } returns TestData.testRules.first()
    every { getRule("testRule1", "test-guild") } returns TestData.testRules.first()
    every { getRule(15, "test-guild") } returns null
}

object TestData {
    object Database {
        val dbTestConfiguration: DatabaseConfiguration = DatabaseConfiguration(databaseName = "test")
    }

    val testRules: List<Rule> = listOf(
            Rule(newId(), "test-guild", 1, "testRule1", "testTitle1", "testDescription1", "",1),
            Rule(newId(), "test-guild", 2, "testRule2", "testTitle2", "testDescription2", "", 2),
            Rule(newId(), "test-guild", 3, "testRule3", "testTitle3", "testDescription3", "", 3)
    )

    val botTestConfiguration: BotConfiguration = BotConfiguration(
            owner = "test-owner",
            whitelist = listOf("testGuild"),
            guilds = listOf(GuildConfiguration("test-guild-id", "test-owner-id")),
            dbConfiguration = Database.dbTestConfiguration
    )

}
