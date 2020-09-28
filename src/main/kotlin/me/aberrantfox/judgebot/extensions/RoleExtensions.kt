package me.aberrantfox.judgebot.extensions

import net.dv8tion.jda.api.entities.Role

fun Role.verboseDescriptor() = "**${this.asMention} :: ${this.name} :: ID :: ${this.id}**"