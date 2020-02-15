package command

import io.mockk.verify
import me.aberrantfox.judgebot.commands.createRulesManagementCommands
import me.aberrantfox.judgebot.localization.Messages
import me.aberrantfox.judgebot.services.DatabaseService
import me.aberrantfox.judgebot.services.EmbedService
import me.aberrantfox.kjdautils.api.dsl.command.ArgumentContainer
import me.aberrantfox.kjdautils.api.dsl.command.CommandEvent
import me.aberrantfox.kjdautils.api.dsl.command.CommandsContainer
import me.aberrantfox.kjdautils.api.dsl.command.NoArg
import me.aberrantfox.kjdautils.internal.services.ConversationService
import mock.commandEventMock
import mock.conversationServiceMock
import mock.databaseServiceMock
import mock.embedServiceMock
import net.dv8tion.jda.api.entities.MessageEmbed
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import org.junit.jupiter.params.provider.ValueSource

class RuleManagementCommandTest {
    lateinit var commands: CommandsContainer
    lateinit var convoService: ConversationService
    lateinit var embedService: EmbedService
    lateinit var databaseService: DatabaseService
    lateinit var commandEvent: CommandEvent<ArgumentContainer>

    @BeforeEach
    fun beforeEach() {
        convoService = conversationServiceMock()
        embedService = embedServiceMock()
        databaseService = databaseServiceMock()
        commandEvent = commandEventMock()
        commands = createRulesManagementCommands(convoService, embedService, databaseService, Messages())
    }

    @ParameterizedTest
    @ValueSource( strings = ["createRule", "updateRule", "deleteRule"])
    fun `Rules can be created, updated, and deleted`(commandName: String) {
        commands[commandName]?.invoke(NoArg(), commandEvent)

        verify(exactly = 1) {
            convoService.createConversation(allAny(), allAny(), allAny())
        }
    }

    @Test
    fun `Rules can be read`() {
        commands["rules"]?.invoke(NoArg(), commandEvent)

        verify(exactly = 1) {
            commandEvent.respond(allAny() as MessageEmbed)
        }
    }
}