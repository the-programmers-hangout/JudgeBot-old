package me.aberrantfox.judgebot.commands

import me.aberrantfox.judgebot.services.PermissionsService
import me.aberrantfox.kjdautils.api.annotation.CommandSet
import me.aberrantfox.kjdautils.api.dsl.command.commands
import me.aberrantfox.kjdautils.api.dsl.embed
import me.aberrantfox.kjdautils.internal.arguments.UserArg
import java.awt.Color

@CommandSet("Utility")
fun createUtilityCommands(permissionsService: PermissionsService) = commands {
    command("whatpfp"){
        description = "Returns the reverse image url of a users profile picture."
        execute(UserArg) {
            val user = it.args.first
            val reverseSearchUrl = "<https://www.google.com/searchbyimage?&image_url=${user.effectiveAvatarUrl}>"

            val embed = embed {
                title = "${user.asTag}'s pfp"
                color = Color.MAGENTA
                description = "[Reverse Search]($reverseSearchUrl)"
                image = user.effectiveAvatarUrl
            }
            it.respond(embed)
        }
    }
}