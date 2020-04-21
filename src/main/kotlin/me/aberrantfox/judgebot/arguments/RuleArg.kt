package me.aberrantfox.judgebot.arguments

import me.aberrantfox.judgebot.services.DatabaseService
import me.aberrantfox.judgebot.services.RuleService
import me.aberrantfox.judgebot.services.database.dataclasses.Rule
import me.aberrantfox.kjdautils.api.dsl.command.CommandEvent
import me.aberrantfox.kjdautils.api.getInjectionObject
import me.aberrantfox.kjdautils.extensions.stdlib.isInteger
import me.aberrantfox.kjdautils.internal.command.ArgumentResult
import me.aberrantfox.kjdautils.internal.command.ArgumentType
import me.aberrantfox.kjdautils.internal.command.ConsumptionType
import net.dv8tion.jda.api.entities.Guild


open class RuleArg(override val name : String = "Rule"): ArgumentType<Rule>() {
    companion object : RuleArg()

    override val examples = arrayListOf("1", "2", "shortName")
    override val consumptionType = ConsumptionType.Single

    override fun convert(arg: String, args: List<String>, event: CommandEvent<*>): ArgumentResult<Rule> {
        val guild : Guild = event.guild ?: return ArgumentResult.Error("Rule arguments cannot be used outside of guilds.")

        val ruleService: RuleService = event.discord.getInjectionObject<RuleService>()
                ?: return ArgumentResult.Error("Could not access database.")

        val rule: Rule? = when {
            arg.isInteger() -> ruleService.getRule(arg.toInt(), guild.id)
            else -> ruleService.getRule(arg, guild.id)
        }
        return if (rule == null) ArgumentResult.Error("Rule not found.") else ArgumentResult.Success(rule)
    }
}
