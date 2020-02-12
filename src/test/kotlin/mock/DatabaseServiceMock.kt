package mock

import io.mockk.*
import me.aberrantfox.judgebot.services.DatabaseService
import me.aberrantfox.kjdautils.api.dsl.command.CommandEvent
import me.aberrantfox.kjdautils.api.getInjectionObject
import me.aberrantfox.kjdautils.discord.Discord

val dbServiceMock = mockk<DatabaseService>() {

}
val discordMock = mockk<Discord>(relaxed = true) {
    every { getInjectionObject<DatabaseService>() } returns dbServiceMock
}
val eventMock = mockk<CommandEvent<*>>(relaxed = true) {
    every { discord } returns discordMock
}

