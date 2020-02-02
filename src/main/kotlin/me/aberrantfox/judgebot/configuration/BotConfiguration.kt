package me.aberrantfox.judgebot.configuration

import me.aberrantfox.kjdautils.api.annotation.Data

@Data("config/config.json")
data class BotConfiguration(
    val owner: String = "insert-owner-id",
    val whitelist: List<String> = listOf("insert-valid-guild-ids"),
    val guilds: List<GuildConfiguration> = listOf(GuildConfiguration()),
    val dbConfiguration: DatabaseConfiguration = DatabaseConfiguration()
)

data class DatabaseConfiguration(
    val address: String = "localhost:27017"
)

data class GuildConfiguration(
    val id: String = "insert-id",
    val rules: List<Rule> = listOf(Rule())
)

data class Rule (
    val id: String = "Each rule must have a unique, unchanging ID",
    val title: String = "A short description of the rule",
    val description: String = "A detailed explanation for clarification purposes",
    val weight: Int = 1
)

fun GuildConfiguration.isRule(index: Int) = rules.size < index

fun GuildConfiguration.getRuleOrNull(index: Int) = rules.getOrNull(index)