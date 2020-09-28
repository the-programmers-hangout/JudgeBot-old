package me.aberrantfox.judgebot.arguments

import me.aberrantfox.judgebot.dataclasses.Rule
import me.aberrantfox.judgebot.services.DatabaseService
import me.jakejmattson.discordkt.api.dsl.arguments.ArgumentResult
import me.jakejmattson.discordkt.api.dsl.arguments.ArgumentType
import me.jakejmattson.discordkt.api.dsl.arguments.Success
import me.jakejmattson.discordkt.api.dsl.arguments.Error
import me.jakejmattson.discordkt.api.dsl.command.CommandEvent
import net.dv8tion.jda.api.entities.Guild

open class RuleArg(override val name : String = "Rule"): ArgumentType<Rule>() {
    override fun generateExamples(event: CommandEvent<*>): MutableList<String> = mutableListOf("1", "2")

    companion object : RuleArg()

    override fun convert(arg: String, args: List<String>, event: CommandEvent<*>): ArgumentResult<Rule> {
        val guild : Guild = event.guild ?: return Error("Rule arguments cannot be used outside of guilds.")

        val databaseService: DatabaseService = event.discord.getInjectionObjects(DatabaseService::class)

        val rule: Rule? = databaseService.rules.getRule(arg.toInt(), guild.id)

        return if (rule == null) Error("Rule not found.") else Success(rule)
    }
}
