package me.aberrantfox.judgebot.services.database.dataclasses

import org.joda.time.DateTime

data class  Note (
        val moderator: String,
        val note: String,
        val dateTime: DateTime
)