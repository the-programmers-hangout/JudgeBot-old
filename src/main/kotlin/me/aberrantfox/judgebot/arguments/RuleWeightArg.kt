package me.aberrantfox.judgebot.arguments

import me.aberrantfox.judgebot.configuration.Constants
import me.aberrantfox.kjdautils.api.dsl.command.CommandEvent
import me.aberrantfox.kjdautils.internal.command.ArgumentResult
import me.aberrantfox.kjdautils.internal.command.ArgumentType
import me.aberrantfox.kjdautils.internal.command.ConsumptionType

open class RuleWeightArg(override val name: String = "Weight") : ArgumentType<Int>() {
    companion object : RuleWeightArg()

    override val examples = arrayListOf("1", "2", "3")
    override val consumptionType = ConsumptionType.Single

    override fun convert(arg: String, args: List<String>, event: CommandEvent<*>): ArgumentResult<Int> {
        val weight = arg.toIntOrNull() ?: return ArgumentResult.Error("Expected an integer number, got $arg")

        if (weight !in Constants.MIN_RULE_WEIGHT..Constants.MAX_RULE_WEIGHT) {
            return ArgumentResult.Error("Please input a weight between ${Constants.MIN_RULE_WEIGHT} and ${Constants.MAX_RULE_WEIGHT}")
        }
        return ArgumentResult.Success(weight)
    }
}
