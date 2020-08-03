package me.aberrantfox.judgebot.utility

import me.aberrantfox.judgebot.dataclasses.PunishmentType
import me.jakejmattson.discordkt.api.dsl.embed.embed
import net.dv8tion.jda.api.entities.MessageEmbed
import java.awt.Color

fun buildInfractionEmbed(userMention: String, reason: String, type: PunishmentType, timeString: String): MessageEmbed? {
    return when (type) {
        PunishmentType.Warn -> buildWarnEmbed(userMention, reason)
        PunishmentType.Mute -> buildMuteEmbed(userMention, timeString!!, reason)
        PunishmentType.Blindfold -> buildBlindfoldEmbed(userMention, timeString!!, reason)
        PunishmentType.BadPfp -> TODO()
        PunishmentType.TemporaryBan -> TODO()
        PunishmentType.AppealableBan -> TODO()
        PunishmentType.PermanentBan -> TODO()
    }
}

fun buildBadPfpEmbed(userMention: String, timeString: String) = embed {
    simpleTitle = "Bad PFP"
    description = """
                    | $userMention, We have flagged your profile picture as inappropriate.
                    | Please change it within the next $timeString or you will be banned
                """.trimMargin()
}

fun buildWarnEmbed(userMention: String, reason: String) = embed {
    simpleTitle = "Mute"
    description = """
                    | $userMention, you have been warned. A warning is a way for TPH staff to inform you that your behaviour needs to change or further infractions will follow.
                    | If you believe this to be in error, please contact Modmail.
                """.trimMargin()


    field {
        name = "__Reason__"
        value = reason
        inline = false
    }
    color = Color.RED
}

fun buildMuteEmbed(userMention: String, timeString: String, reason: String) = embed {
    simpleTitle = "Mute"
    description = """
                    | $userMention, you have been muted. A muted user cannot speak/post in channels.
                    | If you believe this to be in error, please contact Modmail.
                """.trimMargin()

    field {
        name = "Length"
        value = timeString
        inline = false
    }

    field {
        name = "__Reason__"
        value = reason
        inline = false
    }
    color = Color.RED
}

fun buildBlindfoldEmbed(userMention: String, timeString: String, reason: String) = embed {
    simpleTitle = "Blindfold"
    description = """
                    | $userMention, you have been muted. A blindfolded user cannot view channels.
                    | If you believe this to be in error, please contact Modmail.
                """.trimMargin()

    field {
        name = "Length"
        value = timeString
        inline = false
    }

    field {
        name = "__Reason__"
        value = reason
        inline = false
    }
    color = Color.RED
}
