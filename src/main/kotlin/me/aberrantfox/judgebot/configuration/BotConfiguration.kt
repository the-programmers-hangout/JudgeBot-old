package me.aberrantfox.judgebot.configuration

import me.aberrantfox.kjdautils.api.annotation.Data
import org.joda.time.DateTime

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

data class  Note (
        val moderator: String,
        val note: String,
        val dateTime: DateTime
)

enum class InfractionWeight {
    Note, Borderline, Lightly, Clearly, Harshly, Error
}

data class Infraction(
        val moderator: String,
        val reason: String,
        val dateTime: DateTime,
        val weight: InfractionWeight
)

data class GuildMember (
        val userId: String,
        val notes: MutableList<Note> = mutableListOf<Note>(),
        val infractions: MutableList<Infraction> = mutableListOf<Infraction>(),
        var historyCount: Int = 0,
        var points: Int = 0
) {
    fun incrementHistoryCount() {
        this.historyCount += 1
    }
}

fun GuildConfiguration.isRule(index: Int) = rules.size < index

fun GuildConfiguration.getRuleOrNull(index: Int) = rules.getOrNull(index)

fun convertToInfractionType(infraction: String) =
        when(infraction.toLowerCase()) {
            "note" -> InfractionWeight.Note
            "borderline" -> InfractionWeight.Borderline
            "lightly" -> InfractionWeight.Lightly
            "clearly" -> InfractionWeight.Clearly
            "harshly" -> InfractionWeight.Harshly
            else -> InfractionWeight.Error
        }