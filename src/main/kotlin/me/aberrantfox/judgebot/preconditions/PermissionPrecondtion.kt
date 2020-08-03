package me.aberrantfox.judgebot.preconditions

import me.aberrantfox.judgebot.extensions.requiredPermissionLevel
import me.aberrantfox.judgebot.services.DEFAULT_REQUIRED_PERMISSION
import me.aberrantfox.judgebot.services.PermissionsService
import me.jakejmattson.discordkt.api.dsl.command.CommandEvent
import me.jakejmattson.discordkt.api.dsl.preconditions.Fail
import me.jakejmattson.discordkt.api.dsl.preconditions.Pass
import me.jakejmattson.discordkt.api.dsl.preconditions.Precondition
import me.jakejmattson.discordkt.api.dsl.preconditions.PreconditionResult
import me.jakejmattson.discordkt.api.extensions.jda.toMember

class PermissionPrecondtion(private val permissionsService: PermissionsService): Precondition() {
    override fun evaluate(event: CommandEvent<*>): PreconditionResult {
        val command = event.command
        val requiredPermissionLevel = command?.requiredPermissionLevel ?: DEFAULT_REQUIRED_PERMISSION
        val guild = event.guild!!
        val member = event.author.toMember(guild)!!

        if (!permissionsService.hasClearance(member, requiredPermissionLevel))
            return Fail("You do not have the required permissions to perform this action.")

        return Pass
    }

}