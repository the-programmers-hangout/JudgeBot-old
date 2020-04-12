package me.aberrantfox.judgebot.services.database.dataclasses

import org.litote.kmongo.Id
import org.litote.kmongo.newId

//TODO: Figure out how to make composite id with rule number and guild id
data class Rule (
        val _id: Id<Rule> = newId(),
        val guildId: String = "guild-id",
        val number: Int = 0,
        val shortName: String = "short-name",
        val title: String = "A short description of the rule",
        val description: String = "A detailed explanation for clarification purposes",
        val link: String = "",
        val weight: Int = 1
)
