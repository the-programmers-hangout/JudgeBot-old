package mock

import io.mockk.*
import me.aberrantfox.judgebot.services.DatabaseService
import me.aberrantfox.kjdautils.api.dsl.command.ArgumentContainer
import me.aberrantfox.kjdautils.api.dsl.command.CommandEvent
import me.aberrantfox.kjdautils.api.getInjectionObject
import me.aberrantfox.kjdautils.discord.Discord
import net.dv8tion.jda.api.entities.Guild
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

fun guildMock() = spyk<Guild>() {
    every { id } returns "test-guild"
}

fun dbServiceMock() = spyk<DatabaseService>() {
    every { getRule(1, "test-guild") } returns testRules.first()
    every { getRule("testRule1", "test-guild") } returns testRules.first()
    every { getRule(15, "test-guild") } returns null
}

fun discordMock() = spyk<Discord>() {
    every { getInjectionObject<DatabaseService>() } returns dbServiceMock()
}

fun commandEventMock() = spyk<CommandEvent<*>>() {
    every { discord } returns discordMock()
    every { guild } returns guildMock()
}


class MockTests {
    @Test
    fun `local guildMock returns correct value`() {
        val guildMock = spyk<Guild>() {
            every { id } returns "test-guild"
        }
        assertEquals("test-guild", guildMock.id)
    }

    @Test
    fun `global guildMock returns correct value`() {
        assertEquals("test-guild", guildMock().id)
    }
}
