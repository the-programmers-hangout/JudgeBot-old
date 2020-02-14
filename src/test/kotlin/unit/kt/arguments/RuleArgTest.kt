package arguments

import kt.mock.testRules
import me.aberrantfox.judgebot.arguments.RuleArg
import kt.utilities.SimpleArgTest

class RuleArgTest : SimpleArgTest() {
    override val argumentType = RuleArg
    override val validArgs = listOf("1" to testRules.first())
    override val invalidArgs = listOf("15")
}