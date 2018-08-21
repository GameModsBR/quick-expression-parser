@file:JvmName("QuickExpressionLexer")

package br.com.gamemods.qep

import kotlin.jvm.JvmName



private const val EXPECT_IDENTIFIER = 0x1
private const val EXPECT_OPERATOR = 0x2
private const val EXPECT_QUESTION = 0x4
private const val EXPECT_STRING = 0x8

private fun readExpression(
        open: String?, close: Char?,
        input: SingleRollbackIterator<Char>, output: StringBuilder, context: ExpressionContext): Boolean {
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
                    output.append(fullExpression)
                    return false
                }

                val identifier = readIdentifier(input, char).also { fullExpression.append(it, 1, it.length) }
                tokens += identifier
                state = EXPECT_OPERATOR + EXPECT_QUESTION
            }
            '.' -> {
                if (state and EXPECT_OPERATOR == 0) {
                    output.append(fullExpression)
                    return false
                }
                tokens += char
                state = EXPECT_IDENTIFIER
            }
            '?' -> {
                if (state and EXPECT_QUESTION == 0) {
                    output.append(fullExpression)
                    return false
                }
                tokens += char
                state = EXPECT_IDENTIFIER + EXPECT_STRING
                ifCount++
            }
            ':' -> {
                if (ifCount-- == 0 || state and EXPECT_QUESTION == 0) {
                    output.append(fullExpression)
                    return false
                }
                tokens += char
                state = EXPECT_IDENTIFIER + EXPECT_STRING
            }
            '\'', '"' -> {
                if (state and EXPECT_STRING == 0) {
                    output.append(fullExpression)
                    return false
                }

                val content = StringBuilder().also { readRaw(char, input, it, context) }
                if (!input.hasNext()) {
                    output.append(fullExpression).append(content)
                    return false
                }

                val stringClose = input.next()
                if (stringClose != char) {
                    output.append(fullExpression).append(content).append(stringClose)
                    return false
                }

                fullExpression.append(content).append(stringClose)
                tokens += content
                state = EXPECT_QUESTION
            }
            close -> break@input
            else -> if (!char.isWhitespace()) {
                output.append(fullExpression)
                return false
            }
        }
    }

    if (state and EXPECT_QUESTION == 0) {
        output.append(fullExpression)
        return false
    }

    if (tokens.size == 1) {
        val token = tokens.first()
        if (token is StringBuilder) {
            output.append(token)
            return true
        }

        // Tenta obter o valor do contexto, se não tiver então coloca a expressão inteira na saída
        val value = context.consume(tokens.first().toString())
        output.append(value ?: fullExpression)
        return value != null
    }

    return executeExpression(context, tokens, output, fullExpression)
}

private fun executeExpression(
        context: ExpressionContext,
        tokens: MutableList<Any>,
        output: StringBuilder,
        fullExpression: StringBuilder
): Boolean {
    val stack = mutableListOf<Any?>(context)
    val iterator = tokens.iterator()
    tokenScan@
    for (token in iterator) {
        when (token) {
            is String -> {
                stack += (stack.removeLast() as ExpressionContext).provide(token) ?: output.apply {
                    append(fullExpression)
                    return false
                }
            }

            '.' -> {
                stack += (stack.removeLast() as Parameter).context ?: ExpressionContext.EMPTY
            }

            '?' -> {
                if (!(stack.removeLast() as Parameter).value.toBoolean()) {
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
                output.append(fullExpression)
                return false
            }
        }
    }

    val param = stack.single() as Parameter
    output.append(param.value)
    return true
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
        output: StringBuilder,
        context: ExpressionContext
) {
    if (!input.hasNext()) {
        output.append(from ?: return)
        return
    }

    val first = input.next()
    when (first) {
        '{' -> {
            readExpression((from?.toString() ?: "") + '{', '}', input, output, context)
        }
        in 'a'..'z', in 'A'..'Z', in '0'..'9' -> {
            val identifier = readIdentifier(input, first)

            // Tenta obter o valor do contexto, se não tiver então coloca a expressão inteira na saída
            context.consume(identifier)?.let { output.append(it) }
                    ?: output.apply { from?.let { append(it) } }.append(identifier)
        }
    }
}

private fun readEscaped(from: Char?, input: SingleRollbackIterator<Char>, output: StringBuilder) {
    if (!input.hasNext()) {
        output.append(from ?: return)
    } else {
        output.append(input.next())
    }
}

internal fun readRaw(
        ending: Char?,
        input: SingleRollbackIterator<Char>,
        output: StringBuilder,
        context: ExpressionContext
) {
    input.forEach {
        when (it) {
            '#' -> readContext('#', input, output, context)
            '\\' -> readEscaped('\\', input, output)
            ending -> {
                input.rollback()
                return
            }
            else -> output.append(it)
        }
    }
}
