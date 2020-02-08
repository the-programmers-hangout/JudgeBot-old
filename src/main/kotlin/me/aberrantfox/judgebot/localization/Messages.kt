package me.aberrantfox.judgebot.localization

import me.aberrantfox.kjdautils.api.annotation.Data

@Data("config/messages.json")
data class Messages(

    // Rule creation
    val PROMPT_RULE_NUMBER: String = "Please input rule number.",
    val ERROR_RULE_NUMBER_EXISTS: String = "The rule number you have entered already exists in this guild.",
    val PROMPT_RULE_SHORTNAME: String = "Please enter a unique shortname to identify this rule with.",
    val ERROR_RULE_SHORTNAME_EXISTS: String = "The rule shortname you have entered already exists in this guild, or it is too long.(15 char limit)",
    val PROMPT_RULE_TITLE: String = "Please input rule title. This is a short description of the rule.",
    val PROMPT_RULE_DESCRIPTION: String = "Please input rule description. This is a detailed description of the rule.",
    val PROMPT_RULE_WEIGHT: String = "Please input rule weight. Weight decides how harshly users are punished for breaking the rule.",
    val ERROR_RULE_WEIGHT_TOO_LOW: String = "Weight must be at least 1!",
    val RULE_CREATED: String = "Rule created!",

    // Rule deletion
    val PROMPT_RULE_TO_DELETE: String = "Enter the number of the rule you wish to delete.",
    val ERROR_RULE_NUMBER_NOT_EXISTS: String = "The rule number you have entered is not valid.",
    val PROMPT_ARE_YOU_SURE: String = "Are you sure? Y/N",
    //val ERROR_ANS_MUST_BE_Y_OR_N: String = "Answer must be Y or N.\nAre you sure? Y/N",
    val RULE_DELETED: String = "Rule has been deleted!",
    val RULE_NOT_DELETED: String = "No rules have been deleted.",
    val RULE_CHOSEN: String = "You have chosen to delete rule number %0%",

    // Rule update
    val PROMPT_RULE_TO_UPDATE: String = "Enter the number of the rule you wish to update.",
    val PROMPT_UPDATE_RULE_NUMBER: String = "Do you want to update the rule number? Y/N:",
    val PROMPT_UPDATE_RULE_SHORTNAME: String = "Do you want to update the rule shortname? Y/N:",
    val PROMPT_UPDATE_RULE_TITLE: String = "Do you want to update the rule title? Y/N:",
    val PROMPT_UPDATE_RULE_DESCRIPTION: String = "Do you want to update the rule description? Y/N:",
    val PROMPT_UPDATE_RULE_WEIGHT: String = "Do you want to update the rule weight? Y/N:",
    val RULE_UPDATED: String = "Rule updated!",
    val NO_CHANGES_MADE: String = "No changes have been made.",

    // Rule Command Descriptions
    val CREATE_RULE_DESCRIPTION: String = "Use this to create new rules for your guild.",
    val DELETE_RULE_DESCRIPTION: String = "Use this to delete rules for your guild.",
    val DISPLAY_RULES_DESCRIPTION: String = "Displays all the rules and their weights.",
    val UPDATE_RULE_DESCRIPTION: String = "Update a rule for this guild.",
    val DISPLAY_RULE_DESCRIPTION: String = "Display a given rule.",

    // Rule command messages
    val ERROR_COULD_NOT_FIND_RULE: String = "Could not find rule."
    val PROMPT_USER_ID_INFRACTION: String = "Please input the id of the user you would like to issue an Infraction, Strike, Warning. Making a note is also possible.",

    // Infractions
    val PROMPT_USER_INFRACTION_TYPE: String = "Please input the weight of the Infraction you would like to issue. Acceptable values are: Note, Borderline, Lightly, Clearly, Harshly",
    val PROMPT_USER_ADD_PERSONAL_NOTE: String = "Would you like to add a note relating to this Infraction?",
    val PROMPT_PERSONAL_NOTE: String = "Please input the note you would like to add.",
    val PROMPT_INFRACTION_DETAILS: String = "Please input the details of the Infraction"
)

fun String.inject(vararg args: String) : String{
    var injectedString = this
    for (i in args.indices) {
        injectedString = injectedString.replace("%$i%", args[i])
    }
    return injectedString
}
