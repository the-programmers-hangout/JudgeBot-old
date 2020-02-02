package me.aberrantfox.judgebot.localization

import me.aberrantfox.kjdautils.api.annotation.Data

@Data("config/messages.json")
data class Messages(
    val PROMPT_USER_ID_INFRACTION: String = "Please input the id of the user you would like to issue an Infraction, Strike, Warning. Making a note is also possible."
)