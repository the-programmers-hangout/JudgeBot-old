package me.aberrantfox.judgebot.localization

import me.aberrantfox.kjdautils.api.annotation.Data

@Data("config/messages.json")
data class Messages(
    val PROMPT_USER_ID_INFRACTION: String = "Please input the id of the user you would like to issue an Infraction, Strike, Warning. Making a note is also possible.",

    // Rule creation
    val PROMPT_RULE_NUMBER: String = "Please input rule number.",
    val ERROR_RULE_NUMBER_EXISTS: String = "The rule number you have entered already exists in this guild.",
    val PROMPT_RULE_SHORTNAME: String = "Please enter a unique shortname to identify this rule with.",
    val ERROR_RULE_SHORTNAME_EXISTS: String = "The rule shortname you have entered already exists in this guild, or it is too long.(15 char limit)",
    val PROMPT_RULE_TITLE: String = "Please input rule title. This is a short description of the rule.",
    val PROMPT_RULE_DESCRIPTION: String = "Please input rule description. This is a detailed description of the rule.",
    val PROMPT_RULE_WEIGHT: String = "Please input rule weight. Weight decides how harshly users are punished for breaking the rule.",
    val ERROR_RULE_WEIGHT_TOO_LOW: String = "Weight must be at least 1!",

    // Rule deletion
    val PROMPT_RULE_TO_DELETE: String = "Enter the number of the rule you wish to delete.",
    val ERROR_RULE_NUMBER_NOT_EXISTS: String = "The rule number you have entered is not valid.",
    val PROMPT_ARE_YOU_SURE: String = "Are you sure? Y/N",
    //val ERROR_ANS_MUST_BE_Y_OR_N: String = "Answer must be Y or N.\nAre you sure? Y/N",
    val RULE_DELETED: String = "Rule has been deleted!",
    val RULE_NOT_DELETED: String = "No rules have been deleted.",

    // Rule updation
    val PROMPT_RULE_TO_UPDATE: String = "Enter the number of the rule you wish to update.",
    val PROMPT_UPDATE_RULE_NUMBER: String = "Do you want to update the rule number? Y/N:",
    val PROMPT_UPDATE_RULE_SHORTNAME: String = "Do you want to update the rule shortname? Y/N:",
    val PROMPT_UPDATE_RULE_TITLE: String = "Do you want to update the rule title? Y/N:",
    val PROMPT_UPDATE_RULE_DESCRIPTION: String = "Do you want to update the rule description? Y/N:",
    val PROMPT_UPDATE_RULE_WEIGHT: String = "Do you want to update the rule weight? Y/N:"

)