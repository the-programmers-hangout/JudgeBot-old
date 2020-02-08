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
        dbService.addRule(defaultRule)
    }

    @AfterAll
    internal fun tearDown() {
        dbService.deleteRule(defaultRule)
    }

    @Test
    fun ruleByShortName() {
        assertEquals(defaultRule, dbService.getRule(defaultRule.shortName, defaultRule.guildId)) {
            "Failed to retrieve rule by short name."
        }
    }

    @Test
    fun getRuleByNumber() {
        assertEquals(defaultRule, dbService.getRule(defaultRule.number, defaultRule.guildId)) {
            "Failed to retrieve rule by number."
        }
    }

    @Test
    fun getRules() {
        assertEquals(mutableListOf(defaultRule), dbService.getRules(defaultRule.guildId)) {
            "Failed to retrieve list of rules."
        }
    }

    @Test
    fun addRule() {
    }

    @Test
    fun deleteRule() {
    }

    @Test
    fun updateRule() {
    }
}