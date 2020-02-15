package dbtest

import me.aberrantfox.judgebot.configuration.DatabaseConfiguration
import me.aberrantfox.judgebot.services.DatabaseService
import me.aberrantfox.judgebot.services.database.dataclasses.Rule
import mock.TestData
import org.junit.jupiter.api.*

import org.junit.jupiter.api.Assertions.*

/**
 * Integration test, requires an instance of MongoDB
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DatabaseServiceTest {
    val dbService = DatabaseService(TestData.botTestConfiguration)

    @BeforeAll
    internal fun `set up`() {
        dbService.dropRuleCollection()
        for (rule in TestData.testRules) {
            dbService.addRule(rule)
        }
    }

    @AfterAll
    internal fun `tear down`() {
        dbService.dropRuleCollection()
    }

    @Test
    fun `getting rule by short name`() {
        assertEquals(TestData.testRules[0], dbService.getRule("testRule1", "test-guild")) {
            "Failed to retrieve rule by short name."
        }
    }

    @Test
    fun `getting rule by rule number`() {
        assertEquals(TestData.testRules[0], dbService.getRule(1, "test-guild")) {
            "Failed to retrieve rule by number."
        }
    }

    @Test
    fun `getting all rules for guild`() {
        assertEquals(TestData.testRules, dbService.getRules("test-guild")) {
            "Failed to retrieve list of rules."
        }
    }

    @Test
    fun `getting all rules for guild but not other guilds`() {
        dbService.addRule(Rule())
        assertEquals(TestData.testRules, dbService.getRules("test-guild")) {
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