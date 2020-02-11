package me.aberrantfox.judgebot.services.database.dataclasses

data class GuildMember (
        val userId: String,
        val infractions: MutableList<Infraction> = mutableListOf<Infraction>(),
        var historyCount: Int = 0,
        var points: Int = 0
) {
    fun incrementHistoryCount() {
        this.historyCount += 1
    }

    fun addInfraction(infraction: Infraction) {
        this.infractions.add(infraction)
    }
}
