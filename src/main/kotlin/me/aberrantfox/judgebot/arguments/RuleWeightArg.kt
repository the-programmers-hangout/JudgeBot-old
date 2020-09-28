package me.aberrantfox.judgebot.arguments

import me.aberrantfox.judgebot.configuration.Constants
import me.jakejmattson.discordkt.api.dsl.arguments.ArgumentResult
import me.jakejmattson.discordkt.api.dsl.arguments.ArgumentType
import me.jakejmattson.discordkt.api.dsl.arguments.Success
import me.jakejmattson.discordkt.api.dsl.arguments.Error
import me.jakejmattson.discordkt.api.dsl.command.CommandEvent

open class RuleWeightArg(override val name: String = "Weight") : ArgumentType<Int>() {
    override fun generateExamples(event: CommandEvent<*>): MutableList<String> = mutableListOf("1", "2", "3")

    companion object : RuleWeightArg()

    override fun convert(arg: String, args: List<String>, event: CommandEvent<*>): ArgumentResult<Int> {
        val weight = arg.toIntOrNull() ?: return Error("Expected an integer number, got $arg")

        if (weight !in Constants.MIN_RULE_WEIGHT..Constants.MAX_RULE_WEIGHT) {
            return Error("Please input a weight between ${Constants.MIN_RULE_WEIGHT} and ${Constants.MAX_RULE_WEIGHT}")
        }
        return Success(weight)
    }
}
