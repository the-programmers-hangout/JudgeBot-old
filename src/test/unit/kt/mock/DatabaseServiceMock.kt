package kt.mock

import io.mockk.*
import me.aberrantfox.judgebot.services.DatabaseService
import me.aberrantfox.kjdautils.api.dsl.command.CommandEvent
import me.aberrantfox.kjdautils.api.getInjectionObject
import me.aberrantfox.kjdautils.discord.Discord
import net.dv8tion.jda.api.entities.Guild

val guildMock = mockk<Guild>(relaxed = true) {
    every { id } returns "test-guild"
}

val dbServiceMock = mockk<DatabaseService>(relaxed = true) {
    every { getRule(1, "test-guild") } returns testRules.first()
    every { getRule("testRule1", "test-guild") } returns testRules.first()
    every { getRule(15, "test-guild") } returns null
}

val discordMock = mockk<Discord>(relaxed = true) {
    every { getInjectionObject<DatabaseService>() } returns dbServiceMock
}

val commandEventMock = mockk<CommandEvent<*>>(relaxed = true) {
    every { discord } returns discordMock
    every { guild } returns guildMock
}

