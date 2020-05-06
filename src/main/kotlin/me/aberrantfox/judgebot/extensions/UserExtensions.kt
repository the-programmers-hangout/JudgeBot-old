package me.aberrantfox.judgebot.extensions

import me.aberrantfox.kjdautils.extensions.jda.descriptor
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.entities.Role

fun User.verboseDescriptor() = "**${this.asMention} :: ${this.descriptor()}**"

fun Role.verboseDescriptor() = "**${this.asMention} :: ${this.name} :: ${this.id}**"