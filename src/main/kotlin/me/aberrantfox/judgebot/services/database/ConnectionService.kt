package me.aberrantfox.judgebot.services.database

import com.mongodb.MongoClient
import com.mongodb.client.MongoDatabase
import me.aberrantfox.judgebot.configuration.Configuration
import me.jakejmattson.discordkt.api.annotations.Service
import org.litote.kmongo.KMongo

@Service
class ConnectionService(val config: Configuration) {
    private val client: MongoClient = KMongo.createClient(config.dbConfiguration.address)
    val db: MongoDatabase = client.getDatabase(config.dbConfiguration.databaseName)
}
