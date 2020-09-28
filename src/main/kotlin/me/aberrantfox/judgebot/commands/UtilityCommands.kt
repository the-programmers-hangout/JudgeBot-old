package me.aberrantfox.judgebot.commands

import me.aberrantfox.judgebot.services.PermissionsService
import me.jakejmattson.discordkt.api.annotations.CommandSet
import me.jakejmattson.discordkt.api.arguments.*
import me.jakejmattson.discordkt.api.dsl.command.commands
import me.jakejmattson.discordkt.api.dsl.embed.embed
import java.awt.Color

@CommandSet("Utility")
fun createUtilityCommands(permissionsService: PermissionsService) = commands {
    command("whatpfp"){
        description = "Returns the reverse image url of a users profile picture."
        execute(UserArg) {
            val user = it.args.first
            val reverseSearchUrl = "<https://www.google.com/searchbyimage?&image_url=${user.effectiveAvatarUrl}>"

            val embed = embed {
                simpleTitle = "${user.asTag}'s pfp"
                color = Color.MAGENTA
                description = "[Reverse Search]($reverseSearchUrl)"
                image = user.effectiveAvatarUrl
            }
            it.respond(embed)
        }
    }
}