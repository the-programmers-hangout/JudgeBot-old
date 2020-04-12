package command

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import me.aberrantfox.judgebot.arguments.RuleArg
import me.aberrantfox.judgebot.commands.createRulesManagementCommands
import me.aberrantfox.judgebot.localization.Messages
import me.aberrantfox.judgebot.services.EmbedService
import me.aberrantfox.judgebot.services.RuleService
import me.aberrantfox.kjdautils.api.dsl.command.*
import me.aberrantfox.kjdautils.internal.services.ConversationService
import mock.*
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
    lateinit var ruleService: RuleService
    lateinit var commandEvent: CommandEvent<ArgumentContainer>

    @BeforeEach
    fun beforeEach() {
        convoService = conversationServiceMock()
        embedService = embedServiceMock()
        ruleService = databaseServiceMock()
        commandEvent = commandEventMock()
        commands = createRulesManagementCommands(convoService, embedService, Messages())
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

    @ParameterizedTest(name = "{index} : {0}")
    @MethodSource("events")
    fun `Rules can be obtained`(name: String, event: CommandEvent<ArgumentContainer>, valid: Boolean) {
        commands["rule"]?.invoke(SingleArg(RuleArg), event)

        if (valid) {
            verify(exactly = 1) {
                event.respond(allAny() as MessageEmbed)
            }
        } else {
            verify(exactly = 1) {
                event.unsafeRespond(allAny())
            }
        }
    }

    companion object {
        @JvmStatic
        fun events() = arrayOf<Any>(
                arrayOf(
                        "Can display by ID",
                        mockk<CommandEvent<ArgumentContainer>>(relaxed = true) {
                            every { args } returns SingleArg(TestData.testRules.first())
                        },
                        true
                ),
                arrayOf(
                        "Can display by ShortName",
                        mockk<CommandEvent<ArgumentContainer>>(relaxed = true) {
                            every { args } returns SingleArg(TestData.testRules.first())
                        },
                        true
                ),
                arrayOf(
                        "Will display error for invalid ShortName",
                        mockk<CommandEvent<ArgumentContainer>>(relaxed = true) {
                            every { args } returns SingleArg(null)
                        },
                        false
                ),
                arrayOf(
                        "Will display error for invalid rule number",
                        mockk<CommandEvent<ArgumentContainer>>(relaxed = true) {
                            every { args } returns SingleArg(null)
                        },
                        false
                )
        )
    }
}