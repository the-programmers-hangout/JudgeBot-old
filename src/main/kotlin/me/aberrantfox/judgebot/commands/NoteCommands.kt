package me.aberrantfox.judgebot.commands

import me.aberrantfox.judgebot.configuration.BotConfiguration
import me.aberrantfox.judgebot.conversations.InfractionConversation
import me.aberrantfox.judgebot.extensions.requiredPermissionLevel
import me.aberrantfox.judgebot.services.DatabaseService
import me.aberrantfox.judgebot.services.Permission
import me.aberrantfox.judgebot.utility.buildNotesEmbed
import me.aberrantfox.kjdautils.api.annotation.CommandSet
import me.aberrantfox.kjdautils.api.dsl.command.commands
import me.aberrantfox.kjdautils.internal.arguments.IntegerArg
import me.aberrantfox.kjdautils.internal.arguments.MemberArg
import me.aberrantfox.kjdautils.internal.arguments.SentenceArg

@CommandSet("Notes")
fun createNoteCommands(databaseService: DatabaseService,
                       config: BotConfiguration) = commands {
    command("note") {
        description = "Use this to add a note to a user."
        requiresGuild = true
        requiredPermissionLevel = Permission.Staff
        execute(MemberArg, SentenceArg("Note Content")) {
            val (target, note) = it.args
            val user = databaseService.users.getOrCreateUser(target, it.guild!!.id)
            databaseService.users.addNote(it.guild!!, user, note, it.author.id)
            it.respond("Note added.")
        }
    }

    command("viewNotes") {
        description = "Use this to add a note to a user."
        requiresGuild = true
        requiredPermissionLevel = Permission.Staff
        execute(MemberArg) {
            val user = databaseService.users.getOrCreateUser(it.args.first, it.guild!!.id)
            it.respond(buildNotesEmbed(it.args.first, user, it.guild!!, config))
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
