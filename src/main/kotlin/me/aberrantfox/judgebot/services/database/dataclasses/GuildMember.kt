package me.aberrantfox.judgebot.services.database.dataclasses

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
