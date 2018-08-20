package br.com.gamemods.qep

fun Iterator<Char>.parseExpression(vararg context: Pair<String, Any>): String = parseExpression(context.toMap())
fun Iterable<Char>.parseExpression(vararg context: Pair<String, Any>): String = iterator().parseExpression(*context)
fun CharSequence.parseExpression(vararg context: Pair<String, Any>): String = iterator().parseExpression(*context)


fun Iterator<Char>.parseExpression(context: Map<String, Any>): String {
    return StringBuilder().also {
        readRaw(null, SingleRollbackIterator(this), it, ExpressionContext(context))
    }.toString()
}
fun Iterable<Char>.parseExpression(context: Map<String, Any>): String = iterator().parseExpression(context)
fun CharSequence.parseExpression(context: Map<String, Any>): String = iterator().parseExpression(context)

fun Iterator<Char>.parseExpression(context: ParameterProvider): String {
    return StringBuilder().also {
        readRaw(null, SingleRollbackIterator(this), it, ExpressionContext(context))
    }.toString()
}
fun Iterable<Char>.parseExpression(context: ParameterProvider): String = iterator().parseExpression(context)
fun CharSequence.parseExpression(context: ParameterProvider): String = iterator().parseExpression(context)


@Suppress("UNUSED_PARAMETER")
private fun readExpression(
        open: String?, close: Char?,
        input: SingleRollbackIterator<Char>, output: StringBuilder, context: ExpressionContext): Boolean {
    val fullExpression = StringBuilder()
    open?.let { fullExpression.append(it) }

    val tokens = mutableListOf<Any>()
    /**
     * Bits:
     * 0x1: Esperando identificador
     * 0x2: Esperando operador
     * 0x4: Habilita operador '?'
     * 0x8: Habilita o operador ':'
     * 0x10: Habilita string literal
     */
    var state = 0x1

    input@
    for (char in input) {
        fullExpression.append(char)
        if (char.isWhitespace()) {
            // Não faz nada por enquanto
        } else when (char) {
            in 'a'..'z', in 'A'..'Z' -> {
                if (state and 0x1 == 0) {
                    output.append(fullExpression)
                    return false
                }

                val identifier = readIdentifier(input, char).also { fullExpression.append(it, 1, it.length) }
                tokens += identifier
                state = state xor (0x1 + 0x10) or (0x2 + 0x4)
            }
            '.' -> {
                if (state and 0x2 == 0) {
                    output.append(fullExpression)
                    return false
                }
                tokens += char
                state = state xor (0x2 + 0x4) or 0x1
            }
            '?' -> {
                if (state and 0x4 == 0) {
                    output.append(fullExpression)
                    return false
                }
                tokens += char
                state = state xor 0x4 or (0x1 + 0x8 +0x10)
            }
            ':' -> {
                if (state and 0x8 == 0) {
                    output.append(fullExpression)
                    return false
                }
                tokens += char
                state = state xor 0x8 or (0x1 + 0x10)
            }
            '\'', '"' -> {
                if (state and 0x10 == 0) {
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
                state = 0x8
            }
            close -> break@input
        }
    }

    if (state and (0x2 + 0x4) != (0x2 + 0x4) && state != 0x8) {
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
                if(!(stack.removeLast() as Parameter).value.toBoolean()) {
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

private fun readContext(from: Char?, input: SingleRollbackIterator<Char>, output: StringBuilder, context: ExpressionContext) {
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

private fun readRaw(ending: Char?, input: SingleRollbackIterator<Char>, output: StringBuilder, context: ExpressionContext) {
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
