package me.aberrantfox.judgebot.services

import com.mongodb.MongoClient
import me.aberrantfox.judgebot.configuration.BotConfiguration
import me.aberrantfox.judgebot.configuration.DatabaseConfiguration
import me.aberrantfox.judgebot.services.database.dataclasses.Rule
import org.junit.jupiter.api.*

import org.junit.jupiter.api.Assertions.*
import org.litote.kmongo.KMongo

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class DatabaseServiceTest {
    val dbService = DatabaseService(BotConfiguration(dbConfiguration = DatabaseConfiguration(databaseName = "test")))
    val defaultRule = Rule()

    @BeforeAll
    internal fun setUp() {
        for (rule in dbService.getRules("guild-id")) {
            dbService.deleteRule(rule)
        }
        dbService.addRule(defaultRule)
    }

    @AfterAll
    internal fun tearDown() {
        dbService.deleteRule(defaultRule)
    }

    @Test
    fun testGetRuleByShortName() {
        assertEquals(defaultRule, dbService.getRule(defaultRule.shortName, defaultRule.guildId)) {
            "Failed to retrieve rule by short name."
        }
    }

    @Test
    fun testGetRuleByNumber() {
        assertEquals(defaultRule, dbService.getRule(defaultRule.number, defaultRule.guildId)) {
            "Failed to retrieve rule by number."
        }
    }

    @Test
    fun testGetRules() {
        assertEquals(mutableListOf(defaultRule), dbService.getRules(defaultRule.guildId)) {
            "Failed to retrieve list of rules."
        }
    }

    @Test
    fun testAddAndDeleteRule() {
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
    fun testUpdateRule() {
        val testRule = Rule(shortName = "testRule")
        dbService.addRule(testRule)
        val updatedRule = Rule(_id = testRule._id, shortName = "updatedShortName")
        dbService.updateRule(updatedRule)
        assertEquals(null, dbService.getRule(testRule.shortName, testRule.guildId)) {
            "Rule which should be updated is still present."
        }
        assertEquals(updatedRule, dbService.getRule(updatedRule.shortName, updatedRule.guildId)) {
            "Updated rule is not in db."
        }
        dbService.deleteRule(updatedRule)
    }
}