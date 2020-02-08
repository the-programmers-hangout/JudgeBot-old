package me.aberrantfox.judgebot.localization

import me.aberrantfox.kjdautils.api.annotation.Data

@Data("config/messages.json")
data class Messages(
    val PROMPT_USER_ID_INFRACTION: String = "Please input the id of the user you would like to issue an Infraction, Strike, Warning. Making a note is also possible.",
    val PROMPT_USER_INFRACTION_TYPE: String = "Please input the weight of the Infraction you would like to issue. Acceptable values are: Note, Borderline, Lightly, Clearly, Harshly",
    val PROMPT_USER_ADD_PERSONAL_NOTE: String = "Would you like to add a note relating to this Infraction?",
    val PROMPT_PERSONAL_NOTE: String = "Please input the note you would like to add.",
    val PROMPT_INFRACTION_DETAILS: String = "Please input the details of the Infraction"
)