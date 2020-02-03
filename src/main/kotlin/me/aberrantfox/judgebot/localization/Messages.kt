package me.aberrantfox.judgebot.localization

import me.aberrantfox.kjdautils.api.annotation.Data

@Data("config/messages.json")
data class Messages(
    val PROMPT_USER_ID_INFRACTION: String = "Please input the id of the user you would like to issue an Infraction, Strike, Warning. Making a note is also possible.",

    val PROMPT_RULE_ID: String = "Please input rule ID. This is a short, unique rule identifier.",
    val ERROR_RULE_ID_EXISTS: String = "The rule ID you have entered already exists.",
    val PROMPT_RULE_TITLE: String = "Please input rule title. This is a short description of the rule.",
    val PROMPT_RULE_DESCRIPTION: String = "Please input rule description. This is a detailed description of the rule.",
    val PROMPT_RULE_WEIGHT: String = "Please input rule weight. Weight decides how harshly users are punished for breaking the rule.",
    val ERROR_RULE_WEIGHT_TOO_LOW: String = "Weight must be at least 1!"
)