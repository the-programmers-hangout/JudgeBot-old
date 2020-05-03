package me.aberrantfox.judgebot.utility

import me.aberrantfox.judgebot.dataclasses.PunishmentType
import me.aberrantfox.kjdautils.api.dsl.embed
import net.dv8tion.jda.api.entities.MessageEmbed
import java.awt.Color

fun buildInfractionEmbed(userMention: String, timeString: String, reason: String, type: PunishmentType): MessageEmbed {
    return if (type == PunishmentType.Mute) buildMuteEmbed(userMention, timeString, reason)
    else buildBlindfoldEmbed(userMention, timeString, reason)
}

fun buildMuteEmbed(userMention: String, timeString: String, reason: String) = embed {
    title = "Mute"
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
    title = "Blindfold"
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
