package me.aberrantfox.judgebot.arguments

import me.aberrantfox.judgebot.configuration.Rule
import me.aberrantfox.kjdautils.api.dsl.command.CommandEvent
import me.aberrantfox.kjdautils.internal.command.ArgumentResult
import me.aberrantfox.kjdautils.internal.command.ArgumentType
import me.aberrantfox.kjdautils.internal.command.ConsumptionType


open class RuleArg(override val name : String = "Macro"): ArgumentType<Rule>() {
    companion object : RuleArg()

    override val examples = arrayListOf("1", "2", "3", "4", "")
    override val consumptionType = ConsumptionType.Single

    override fun convert(arg: String, args: List<String>, event: CommandEvent<*>): ArgumentResult<Rule> {
        val guild = event.guild ?: return ArgumentResult.Error("Rule arguments cannot be used outside of guilds.")

        return ArgumentResult.Success(Rule(), listOf("a"))
    }
}
