package me.aberrantfox.judgebot.arguments

import me.aberrantfox.judgebot.services.DatabaseService
import me.aberrantfox.judgebot.services.database.dataclasses.Rule
import me.aberrantfox.kjdautils.api.dsl.command.CommandEvent
import me.aberrantfox.kjdautils.discord.Discord
import me.aberrantfox.kjdautils.api.getInjectionObject
import me.aberrantfox.kjdautils.extensions.stdlib.isInteger
import me.aberrantfox.kjdautils.internal.command.ArgumentResult
import me.aberrantfox.kjdautils.internal.command.ArgumentType
import me.aberrantfox.kjdautils.internal.command.ConsumptionType


open class RuleArg(override val name : String = "Macro"): ArgumentType<Rule>() {
    companion object : RuleArg()

    override val examples = arrayListOf("1", "2", "3", "4")
    override val consumptionType = ConsumptionType.Single


    override fun convert(arg: String, args: List<String>, event: CommandEvent<*>): ArgumentResult<Rule> {
        val guild = event.guild ?: return ArgumentResult.Error("Rule arguments cannot be used outside of guilds.")

        val dbService: DatabaseService = event.discord.getInjectionObject<DatabaseService>()
                ?: return ArgumentResult.Error("Could not access database.")

        val rule: Rule? = when {
            arg.isInteger() -> dbService.getRule(arg.toInt(), event.guild!!.id)
            else -> dbService.getRule(arg, event.guild!!.id)
        }
        return if (rule == null) ArgumentResult.Error("Rule not found.") else ArgumentResult.Success(rule)
    }
}
