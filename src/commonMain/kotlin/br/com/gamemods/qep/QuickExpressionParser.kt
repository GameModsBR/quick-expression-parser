@file:JvmName("QuickExpressionLexer")

package br.com.gamemods.qep

import java.util.*

private const val EXPECT_IDENTIFIER = 0x1
private const val EXPECT_OPERATOR = 0x2
private const val EXPECT_QUESTION = 0x4
private const val EXPECT_STRING = 0x8

private fun readExpression(
    open: String?,
    close: Char?,
    input: SingleRollbackIterator<Char>,
    appender: (Any?) -> Unit,
    context: ExpressionContext
): Boolean {
    val fullExpression = StringBuilder()
    open?.let { fullExpression.append(it) }

    val tokens = mutableListOf<Any>()
    var state = EXPECT_IDENTIFIER
    var ifCount = 0

    input@
    for (char in input) {
        fullExpression.append(char)
        when (char) {
            in 'a'..'z', in 'A'..'Z' -> {
                if (state and EXPECT_IDENTIFIER == 0) {
                    appender(fullExpression)
                    return false
                }

                val identifier = readIdentifier(input, char).also { fullExpression.append(it, 1, it.length) }
                tokens += identifier
                state = EXPECT_OPERATOR + EXPECT_QUESTION
            }
            '.' -> {
                if (state and EXPECT_OPERATOR == 0) {
                    appender(fullExpression)
                    return false
                }
                tokens += char
                state = EXPECT_IDENTIFIER
            }
            '?' -> {
                if (state and EXPECT_QUESTION == 0) {
                    appender(fullExpression)
                    return false
                }
                tokens += char
                state = EXPECT_IDENTIFIER + EXPECT_STRING
                ifCount++
            }
            ':' -> {
                if (ifCount-- == 0 || state and EXPECT_QUESTION == 0) {
                    appender(fullExpression)
                    return false
                }
                tokens += char
                state = EXPECT_IDENTIFIER + EXPECT_STRING
            }
            '\'', '"' -> {
                if (state and EXPECT_STRING == 0) {
                    appender(fullExpression)
                    return false
                }

                val content = StringBuilder().also { readRaw(char, input, it, context) }
                if (!input.hasNext()) {
                    appender(fullExpression)
                    appender(content)
                    return false
                }

                val stringClose = input.next()
                if (stringClose != char) {
                    appender(fullExpression)
                    appender(content)
                    appender(stringClose)
                    return false
                }

                fullExpression.append(content).append(stringClose)
                tokens += content
                state = EXPECT_QUESTION
            }
            close -> break@input
            else -> if (!char.isWhitespace()) {
                appender(fullExpression)
                return false
            }
        }
    }

    if (state and EXPECT_QUESTION == 0) {
        appender(fullExpression)
        return false
    }

    if (tokens.size == 1) {
        val token = tokens.first()
        if (token is StringBuilder) {
            appender(token)
            return true
        }

        // Tenta obter o valor do contexto, se não tiver então coloca a expressão inteira na saída
        val value = context.consume(tokens.first().toString())
        appender(value ?: fullExpression)
        return value != null
    }

    return executeExpression(context, tokens, appender, fullExpression)
}

private fun executeExpression(
    context: ExpressionContext,
    tokens: MutableList<Any>,
    appender: (Any?) -> Unit,
    fullExpression: StringBuilder
): Boolean {
    val stack = mutableListOf<Any?>(context)
    val iterator = tokens.iterator()
    tokenScan@
    for (token in iterator) {
        when (token) {
            is String -> {
                stack += (stack.removeLast() as ExpressionContext).provide(token) ?: run {
                    appender(fullExpression)
                    return false
                }
            }

            '.' -> {
                stack += (stack.removeLast() as Parameter).context ?: ExpressionContext.EMPTY
            }

            '?' -> {
                if (!(stack.removeLast() as Parameter).value(context.locale).toBoolean(context.locale)) {
                    var questionMarks = 1
                    skip@
                    for (skipped in iterator) {
                        when (skipped) {
                            '?' -> questionMarks++
                            ':' -> if (--questionMarks == 0) {
                                break@skip
                            }
                        }
                    }
                }
                stack += context
            }

            ':' -> break@tokenScan

            is StringBuilder -> {
                stack.removeLast() as ExpressionContext
                stack += ProcessedParameter(token.toString())
            }

            else -> {
                appender(fullExpression)
                return false
            }
        }
    }

    val param = stack.single() as Parameter
    appender(param.value(context.locale))
    return true
}

private tailrec fun Any.toBoolean(locale: Locale): Boolean {
    return when (this) {
        is Boolean -> this
        is String -> toBoolean()
        is LocalizedParameterProvider -> (value(locale) ?: return false).toBoolean(locale)
        is ParameterProvider -> (value() ?: return false).toBoolean(locale)
        else -> toString().toBoolean()
    }
}

private fun <E> MutableList<E>.removeLast(): E = removeAt(lastIndex)

private fun readIdentifier(input: SingleRollbackIterator<Char>, first: Char): String
        = StringBuilder().append(first).also { readIdentifier(input, it) }.toString()

private fun readIdentifier(input: SingleRollbackIterator<Char>, output: StringBuilder) {
    var allowNumber = false
    input.forEach {
        when (it) {
            in 'a'..'z', in 'A'..'Z' -> output.append(it).apply { allowNumber = true }
            in '0'..'9' -> if (allowNumber) output.append(it) else {
                input.rollback()
                return
            }
            else -> {
                input.rollback()
                return
            }
        }
    }
}

private fun readContext(
    from: Char?,
    input: SingleRollbackIterator<Char>,
    appender: (Any?) -> Unit,
    context: ExpressionContext
) {
    if (!input.hasNext()) {
        appender(from ?: return)
        return
    }

    val first = input.next()
    when (first) {
        '{' -> {
            readExpression((from?.toString() ?: "") + '{', '}', input, appender, context)
        }
        in 'a'..'z', in 'A'..'Z', in '0'..'9' -> {
            val identifier = readIdentifier(input, first)

            // Tenta obter o valor do contexto, se não tiver então coloca a expressão inteira na saída
            context.consume(identifier)?.let(appender)
                    ?: run {
                        from?.let(appender)
                        appender(identifier)
                    }
        }
        else -> appender(from ?: return)
    }
}

private fun readEscaped(from: Char?, input: SingleRollbackIterator<Char>, appender: (Any?) -> Unit) {
    if (!input.hasNext()) {
        appender(from ?: return)
    } else {
        appender(input.next())
    }
}

internal fun readRaw(
    ending: Char?,
    input: SingleRollbackIterator<Char>,
    output: StringBuilder,
    context: ExpressionContext
) = readRaw(ending, input, context) {
    output.append(it)
}

internal fun readRaw(
        ending: Char?,
        input: SingleRollbackIterator<Char>,
        context: ExpressionContext,
        appender: (Any?) -> Unit
) {
    input.forEach {
        when (it) {
            '#' -> readContext('#', input, appender, context)
            '\\' -> readEscaped('\\', input, appender)
            ending -> {
                input.rollback()
                return
            }
            else -> appender(it)
        }
    }
}
