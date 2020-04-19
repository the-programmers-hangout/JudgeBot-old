package me.aberrantfox.judgebot.services.database.dataclasses

import me.aberrantfox.judgebot.configuration.BotConfiguration

data class GuildMember (
        val userId: String,
        val infractions: MutableList<Infraction> = mutableListOf<Infraction>(),
        var historyCount: Int = 0,
        var points: Int = 0,
        var lastInfraction: Long = 0
) {
    fun incrementHistoryCount() {
        this.historyCount += 1
    }

    fun addInfraction(infraction: Infraction) {
        this.infractions.add(infraction)
        this.lastInfraction = infraction.dateTime
    }

    fun getStatus(): String {
        var status: String =""
        for (entry in BotConfiguration().security.pointsToStatusMap.toSortedMap()) {
            if (this.points >= entry.key) status = entry.value
        }
        return status
    }
}
