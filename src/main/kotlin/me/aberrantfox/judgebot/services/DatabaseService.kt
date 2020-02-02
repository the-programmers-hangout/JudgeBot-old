package me.aberrantfox.judgebot.services

import com.mongodb.MongoClient
import me.aberrantfox.judgebot.configuration.BotConfiguration
import me.aberrantfox.kjdautils.api.annotation.Service
import org.litote.kmongo.KMongo

@Service
class DatabaseService(val config: BotConfiguration) {
    private val client: MongoClient = KMongo.createClient(config.dbConfiguration.address)
    private val db = client.getDatabase("judgebot")
    private val infractionCollection = db.getCollection("infractionCollection")

    fun insertUserRecord() {

    }

    fun getUserHistory() {
        TODO("Implement history embed")
    }
}