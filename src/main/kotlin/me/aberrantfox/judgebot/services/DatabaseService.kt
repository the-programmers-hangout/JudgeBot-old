package me.aberrantfox.judgebot.services

import com.mongodb.MongoClient
import com.mongodb.client.result.DeleteResult
import me.aberrantfox.judgebot.configuration.BotConfiguration
import me.aberrantfox.judgebot.services.database.dataclasses.Rule
import me.aberrantfox.kjdautils.api.annotation.Service
import org.litote.kmongo.*

@Service
open class DatabaseService(val config: BotConfiguration) {
    private val client: MongoClient = KMongo.createClient(config.dbConfiguration.address)
    val db = client.getDatabase(config.dbConfiguration.databaseName)
}
