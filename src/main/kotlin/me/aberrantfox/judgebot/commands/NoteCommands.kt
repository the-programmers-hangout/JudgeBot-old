package me.aberrantfox.judgebot.commands

import me.aberrantfox.judgebot.configuration.Configuration
import me.aberrantfox.judgebot.extensions.requiredPermissionLevel
import me.aberrantfox.judgebot.services.DatabaseService
import me.aberrantfox.judgebot.services.Permission
import me.jakejmattson.discordkt.api.annotations.CommandSet
import me.jakejmattson.discordkt.api.arguments.*
import me.jakejmattson.discordkt.api.dsl.command.commands

@CommandSet("Notes")
fun createNoteCommands(databaseService: DatabaseService,
                       config: Configuration) = commands {
    command("note") {
        description = "Use this to add a note to a user."
        requiresGuild = true
        requiredPermissionLevel = Permission.Staff
        execute(MemberArg, EveryArg("Note Content")) {
            val (target, note) = it.args
            val user = databaseService.users.getOrCreateUser(target, it.guild!!.id)
            databaseService.users.addNote(it.guild!!, user, note, it.author.id)
            it.respond("Note added.")
        }
    }

    command("deleteNote") {
        description = "Use this to add a delete a note from a user."
        requiresGuild = true
        requiredPermissionLevel = Permission.Staff
        execute(MemberArg, IntegerArg) {
            val (target, noteId) = it.args
            val user = databaseService.users.getOrCreateUser(target, it.guild!!.id)
            if (user.getGuildInfo(it.guild!!.id)!!.notes.isEmpty()) return@execute it.respond("User has no notes.")
            databaseService.users.deleteNote(it.guild!!, user, noteId)
            it.respond("Note deleted.")
        }
    }
}
