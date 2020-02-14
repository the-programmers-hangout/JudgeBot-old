package utilities

import me.aberrantfox.kjdautils.internal.command.ArgumentResult
import me.aberrantfox.kjdautils.internal.command.ArgumentType
import mock.commandEventMock

fun ArgumentType<*>.convertToSuccess(input: String) = attemptConvert(input) as ArgumentResult.Success
fun ArgumentType<*>.convertToError(input: String) = attemptConvert(input) as ArgumentResult.Error

fun ArgumentType<*>.attemptConvert(input: String) : ArgumentResult<*> {
    val split = input.split(" ")
    return convert(split.first(), split, commandEventMock)
}