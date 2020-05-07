package me.aberrantfox.judgebot.configuration

import me.aberrantfox.judgebot.dataclasses.PunishmentConfig
import me.aberrantfox.judgebot.dataclasses.PunishmentType
import me.aberrantfox.kjdautils.api.annotation.Data

@Data("config/config.json")
data class BotConfiguration(
        val owner: String = "insert-owner-id",
        val whitelist: List<String> = listOf("insert-valid-guild-ids"),
        val guilds: List<GuildConfiguration> = listOf(GuildConfiguration()),
        val dbConfiguration: DatabaseConfiguration = DatabaseConfiguration(),
        var prefix: String = "judge!"
) {
        fun getGuildConfig(guildId: String) = guilds.firstOrNull { it.id == guildId }
}

data class DatabaseConfiguration(
        val address: String = "localhost:27017",
        val databaseName: String = "judgebot"
)

data class GuildConfiguration(
        val id: String = "insert-id",
        val owner: String = "insert-owner-id",
        var adminRole: String = "",
        var moderatorRole: String = "",
        var staffRole: String = "",
        val security: Security = Security()
)

data class Security(
        val pointsToStatusMap: HashMap<Int, String> = hashMapOf(
                0 to "Clear",
                15 to "Green",
                30 to "Yellow",
                45 to "Orange",
                60 to "Red"
        ),
        val pointsToPunishmentMap: HashMap<Int, PunishmentConfig> = hashMapOf(
                0 to PunishmentConfig(PunishmentType.Warn),
                3 to PunishmentConfig(PunishmentType.Mute, 60 * 5),
                5 to PunishmentConfig(PunishmentType.Mute, 60 * 30),
                8 to PunishmentConfig(PunishmentType.Mute, 60 * 60),
                10 to PunishmentConfig(PunishmentType.Mute, 60 * 60 * 4),
                12 to PunishmentConfig(PunishmentType.Mute, 60 * 60 * 16),
                18 to PunishmentConfig(PunishmentType.Mute, 60 * 60 * 24),
                20 to PunishmentConfig(PunishmentType.Mute, 60 * 60 * 48),
                25 to PunishmentConfig(PunishmentType.Blindfold, 60 * 60 * 24),
                30 to PunishmentConfig(PunishmentType.Blindfold, 60 * 60 * 48),
                35 to PunishmentConfig(PunishmentType.Blindfold, 60 * 60 * 24 * 7),
                40 to PunishmentConfig(PunishmentType.TemporaryBan, 60 * 60 * 24 * 14),
                45 to PunishmentConfig(PunishmentType.TemporaryBan, 60 * 60 * 24 * 14),
                50 to PunishmentConfig(PunishmentType.TemporaryBan, 60 * 60 * 24 * 30),
                55 to PunishmentConfig(PunishmentType.AppealableBan, 60 * 60 * 24 * 30),
                60 to PunishmentConfig(PunishmentType.AppealableBan, 60 * 60 * 24 * 90),
                65 to PunishmentConfig(PunishmentType.PermanentBan)
        ),
        val pointsMax: Int = 65,
        val mutedRole: String = "muted",
        val blindfoldRole: String = "blindfolded"
)







