package me.aberrantfox.judgebot.services.mock

import com.mongodb.MongoClient
import com.mongodb.client.FindIterable
import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import io.mockk.impl.annotations.MockK
import io.mockk.*
import io.mockk.impl.annotations.InjectMockKs
import me.aberrantfox.judgebot.configuration.BotConfiguration
import me.aberrantfox.judgebot.services.DatabaseService
import me.aberrantfox.judgebot.services.database.dataclasses.Rule
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.litote.kmongo.findOne
import org.litote.kmongo.getCollection

class DatabaseServiceMockTest {
    @MockK
    lateinit var client: MongoClient

    @MockK
    lateinit var db: MongoDatabase

    @MockK
    lateinit var ruleCollection: MongoCollection<Rule>

    @InjectMockKs
    val dbService = DatabaseService(BotConfiguration())

    val defaultRule = Rule()

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        every { client.getDatabase("judgebot") } returns db
        every { db.getCollection<Rule>("ruleCollection") } returns ruleCollection
        every { ruleCollection.findOne() } returns defaultRule

    }
}