package kt.mock

import me.aberrantfox.judgebot.services.database.dataclasses.Rule
import org.litote.kmongo.newId

val testRules = listOf(
        Rule(newId(), "test-guild", 1, "testRule1", "testTitle1", "testDescription1", 1),
        Rule(newId(), "test-guild", 2, "testRule2", "testTitle2", "testDescription2", 2),
        Rule(newId(), "test-guild", 3, "testRule3", "testTitle3", "testDescription3", 3)
)