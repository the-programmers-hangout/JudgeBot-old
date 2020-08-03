package me.aberrantfox.judgebot.arguments

import me.aberrantfox.judgebot.services.PermissionsService
import me.jakejmattson.discordkt.api.dsl.arguments.*
import me.jakejmattson.discordkt.api.dsl.command.CommandEvent
import me.jakejmattson.discordkt.api.extensions.stdlib.trimToID
import me.jakejmattson.discordkt.api.extensions.jda.tryRetrieveSnowflake
import net.dv8tion.jda.api.entities.Member

open class LowerMemberArg(override val name : String = "Lower Ranked member") : ArgumentType<Member>() {
    companion object : LowerMemberArg()

    override fun generateExamples(event: CommandEvent<*>)
            = mutableListOf("@User", "197780697866305536", "302134543639511050")

    override fun convert(arg: String, args: List<String>, event: CommandEvent<*>): ArgumentResult<Member> {
        val permissions = event.discord.getInjectionObjects(PermissionsService::class)

        val retrieved =  event.discord.jda.tryRetrieveSnowflake {
            event.guild?.getMemberById(arg.trimToID())
        } as Member? ?: return Error("Couldn't retrieve member: $arg")

        val author = event.guild!!.getMember(event.author)!!

        return when {
            author.isHigherRankedThan(permissions, retrieved)
            -> Error("You don't have the permission to use this command on the target user.")
            else -> Success(retrieved)
        }
    }
}

private fun Member.isHigherRankedThan(permissions: PermissionsService, targetMember: Member) =
        permissions.getPermissionLevel(this) > permissions.getPermissionLevel(targetMember)