package me.aberrantfox.judgebot

import com.google.gson.Gson
import me.aberrantfox.kjdautils.api.dsl.PrefixDeleteMode
import me.aberrantfox.kjdautils.api.dsl.embed
import me.aberrantfox.kjdautils.api.startBot
import me.aberrantfox.kjdautils.extensions.jda.fullName
import java.awt.Color
import kotlin.system.exitProcess

data class Properties(val author: String, val version: String, val kutils: String, val repository: String)

private val propFile = Properties::class.java.getResource("/properties.json").readText()
val project = Gson().fromJson(propFile, Properties::class.java)

fun main(args: Array<String>) {
    val token = args.firstOrNull()

    if(token == null || token == "UNSET") {
        println("Please specify bot_Token ")
        exitProcess(-1)
    }

    startBot(token,"me.aberrantfox.judgebot.") {

    }
}
