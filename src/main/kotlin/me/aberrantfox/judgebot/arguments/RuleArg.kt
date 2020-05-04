package me.aberrantfox.judgebot.arguments

import me.aberrantfox.judgebot.services.database.RuleOperations
import me.aberrantfox.judgebot.dataclasses.Rule
import me.aberrantfox.judgebot.services.DatabaseService
import me.aberrantfox.kjdautils.api.dsl.command.CommandEvent
import me.aberrantfox.kjdautils.api.getInjectionObject
import me.aberrantfox.kjdautils.extensions.stdlib.isInteger
import me.aberrantfox.kjdautils.internal.command.ArgumentResult
import me.aberrantfox.kjdautils.internal.command.ArgumentType
import me.aberrantfox.kjdautils.internal.command.ConsumptionType
import net.dv8tion.jda.api.entities.Guild

open class RuleArg(override val name : String = "Rule"): ArgumentType<Rule>() {
    override fun generateExamples(event: CommandEvent<*>): MutableList<String> = mutableListOf("1", "2", "shortName")

    companion object : RuleArg()
    override val consumptionType = ConsumptionType.Single

    override fun convert(arg: String, args: List<String>, event: CommandEvent<*>): ArgumentResult<Rule> {
        val guild : Guild = event.guild ?: return ArgumentResult.Error("Rule arguments cannot be used outside of guilds.")

        val databaseService: DatabaseService = event.discord.getInjectionObject<DatabaseService>()
                ?: return ArgumentResult.Error("Could not access database.")

        val rule: Rule? = when {
            arg.isInteger() -> databaseService.rules.getRule(arg.toInt(), guild.id)
            else -> databaseService.rules.getRule(arg, guild.id)
        }
        return if (rule == null) ArgumentResult.Error("Rule not found.") else ArgumentResult.Success(rule)
    }
}
