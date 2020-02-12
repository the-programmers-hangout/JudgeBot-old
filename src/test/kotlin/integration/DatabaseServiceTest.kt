package integration

import me.aberrantfox.judgebot.configuration.BotConfiguration
import me.aberrantfox.judgebot.configuration.DatabaseConfiguration
import me.aberrantfox.judgebot.configuration.GuildConfiguration
import me.aberrantfox.judgebot.services.DatabaseService
import me.aberrantfox.judgebot.services.database.dataclasses.Rule
import org.junit.jupiter.api.*

import org.junit.jupiter.api.Assertions.*
import org.litote.kmongo.newId

/**
 * Integration test, requires an instance of MongoDB
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class DatabaseServiceTest {
    val dbTestConfiguration: DatabaseConfiguration = DatabaseConfiguration(databaseName = "test")
    val botTestConfiguration: BotConfiguration = BotConfiguration(
            owner = "test-owner",
            whitelist = listOf("testGuild"),
            guilds = listOf(GuildConfiguration("test-guild-id", "test-owner-id")),
            dbConfiguration = dbTestConfiguration
    )
    val dbService = DatabaseService(botTestConfiguration)
    val testRules: List<Rule> = listOf(
            Rule(newId(), "test-guild", 1, "testRule1", "testTitle1", "testDescription1", 1),
            Rule(newId(), "test-guild", 2, "testRule2", "testTitle2", "testDescription2", 2),
            Rule(newId(), "test-guild", 3, "testRule3", "testTitle3", "testDescription3", 3)
    )

    @BeforeAll
    internal fun `set up`() {
        dbService.dropRuleCollection()
        for (rule in testRules) {
            dbService.addRule(rule)
        }
    }

    @AfterAll
    internal fun `tear down`() {
        dbService.dropRuleCollection()
    }

    @Test
    fun `getting rule by short name`() {
        assertEquals(testRules[0], dbService.getRule("testRule1", "test-guild")) {
            "Failed to retrieve rule by short name."
        }
    }

    @Test
    fun `getting rule by rule number`() {
        assertEquals(testRules[0], dbService.getRule(1, "test-guild")) {
            "Failed to retrieve rule by number."
        }
    }

    @Test
    fun `getting all rules for guild`() {
        assertEquals(testRules, dbService.getRules("test-guild")) {
            "Failed to retrieve list of rules."
        }
    }

    @Test
    fun `getting all rules for guild but not other guilds`() {
        dbService.addRule(Rule())
        assertEquals(testRules, dbService.getRules("test-guild")) {
            "Failed to retrieve list of rules."
        }
    }

    @Test
    fun `add and delete rule`() {
        val testRule = Rule(shortName = "testRule")
        dbService.addRule(testRule)
        assertEquals(testRule, dbService.getRule(testRule.shortName, testRule.guildId)) {
            "Failed to add (or retrieve) rule."
        }
        dbService.deleteRule(testRule)
        assertEquals(null, dbService.getRule(testRule.shortName, testRule.guildId)) {
            "Failed to delete rule."
        }
    }

    @Test
    fun `updating a rule`() {
        val testRule = Rule(shortName = "testRule")
        dbService.addRule(testRule)
        val updatedRule = Rule(_id = testRule._id, shortName = "updatedShortName")
        dbService.updateRule(updatedRule)
        assertEquals(null, dbService.getRule(testRule.shortName, testRule.guildId)) {
            "Rule which should be updated is still present."
        }
        assertEquals(updatedRule, dbService.getRule(updatedRule.shortName, updatedRule.guildId)) {
            "Updated rule is not in database."
        }
        dbService.deleteRule(updatedRule)
    }
}