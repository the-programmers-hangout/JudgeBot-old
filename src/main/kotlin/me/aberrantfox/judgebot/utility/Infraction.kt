package me.aberrantfox.judgebot.utility

import me.aberrantfox.kjdautils.api.dsl.embed
import java.awt.Color

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
