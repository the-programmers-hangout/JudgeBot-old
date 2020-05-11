package me.aberrantfox.judgebot.utility

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.Role

fun applyRoleWithTimer(member: Member, role: Role, millis: Long, fn: (Member) -> Unit): Job {
    member.guild.addRoleToMember(member, role).queue()
    return GlobalScope.launch {
        delay(millis)
        fn(member)
    }
}
