package me.aberrantfox.judgebot.extensions

import me.jakejmattson.discordkt.api.extensions.jda.fullName
import net.dv8tion.jda.api.entities.User

fun User.verboseDescriptor() = "**${this.asMention} :: ${this.fullName()} :: ID :: ${this.id}**"