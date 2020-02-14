package utilities

import me.aberrantfox.kjdautils.internal.command.ArgumentResult
import me.aberrantfox.kjdautils.internal.command.ArgumentType
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.fail

abstract class SimpleArgTest {
    abstract val argumentType: ArgumentType<*>
    abstract val validArgs: List<Pair<String, *>>
    abstract val invalidArgs: List<String>

    @TestFactory
    fun `valid input`() = validArgs.map { (input, expected) ->
        DynamicTest.dynamicTest("\"$input\" -> $expected") {
            Assertions.assertEquals(expected, argumentType.convertToSuccess(input).result)
        }
    }

    @TestFactory
    fun `invalid input`() = invalidArgs.map { input ->
        when (val conversionResult = argumentType.attemptConvert(input)) {
            is ArgumentResult.Error -> {
                DynamicTest.dynamicTest("\"$input\" -> ${conversionResult.error}") {
                    Assertions.assertTrue(true)
                }
            }
            is ArgumentResult.Success -> {
                DynamicTest.dynamicTest("\"$input\" -> ${conversionResult.result}") {
                    fail { "Conversion succeeded, but was expected to fail." }
                }
            }
        }
    }

}