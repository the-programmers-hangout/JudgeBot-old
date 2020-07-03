package dbtest

import me.aberrantfox.judgebot.services.DatabaseService
import me.aberrantfox.judgebot.dataclasses.Rule
import mock.TestData
import org.junit.jupiter.api.*

import org.junit.jupiter.api.Assertions.*

/**
 * Integration test, requires an instance of MongoDB
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DatabaseServiceTest {
    val dbService = DatabaseService(TestData.botTestConfiguration, TestData.punishments, TestData.users, TestData.rules, TestData.guilds)

    @BeforeAll
    internal fun `set up`() {
        dbService.rules.dropRuleCollection()
        for (rule in TestData.testRules) {
            dbService.rules.addRule(rule)
        }
    }

    @AfterAll
    internal fun `tear down`() {
        dbService.rules.dropRuleCollection()
    }

    @Test
    fun `getting rule by short name`() {
        assertEquals(TestData.testRules[0], dbService.rules.getRule("testRule1", "test-guild")) {
            "Failed to retrieve rule by short name."
        }
    }

    @Test
    fun `getting rule by rule number`() {
        assertEquals(TestData.testRules[0], dbService.rules.getRule(1, "test-guild")) {
            "Failed to retrieve rule by number."
        }
    }

    @Test
    fun `getting all rules for guild`() {
        assertEquals(TestData.testRules, dbService.rules.getRules("test-guild")) {
            "Failed to retrieve list of rules."
        }
    }

    @Test
    fun `getting all rules for guild but not other guilds`() {
        dbService.rules.addRule(Rule())
        assertEquals(TestData.testRules, dbService.rules.getRules("test-guild")) {
            "Failed to retrieve list of rules."
        }
    }

    @Test
    fun `add and delete rule`() {
        val testRule = Rule(shortName = "testRule")
        dbService.rules.addRule(testRule)
        assertEquals(testRule, dbService.rules.getRule(testRule.shortName, testRule.guildId)) {
            "Failed to add (or retrieve) rule."
        }
        dbService.rules.deleteRule(testRule)
        assertEquals(null, dbService.rules.getRule(testRule.shortName, testRule.guildId)) {
            "Failed to delete rule."
        }
    }

    @Test
    fun `updating a rule`() {
        val testRule = Rule(shortName = "testRule")
        dbService.rules.addRule(testRule)
        val updatedRule = Rule(_id = testRule._id, shortName = "updatedShortName")
        dbService.rules.updateRule(updatedRule)
        assertEquals(null, dbService.rules.getRule(testRule.shortName, testRule.guildId)) {
            "Rule which should be updated is still present."
        }
        assertEquals(updatedRule, dbService.rules.getRule(updatedRule.shortName, updatedRule.guildId)) {
            "Updated rule is not in database."
        }
        dbService.rules.deleteRule(updatedRule)
    }
}