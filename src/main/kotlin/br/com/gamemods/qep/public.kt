package br.com.gamemods.qep

fun Iterator<Char>.parseExpression(vararg context: Pair<String, Any>): String = parseExpression(context.toMap())
fun Iterable<Char>.parseExpression(vararg context: Pair<String, Any>): String = iterator().parseExpression(context.toMap())
fun CharSequence.parseExpression(vararg context: Pair<String, Any>): String = iterator().parseExpression(context.toMap())

fun Iterable<Char>.parseExpression(context: Map<String, Any>): String = iterator().parseExpression(context)
fun CharSequence.parseExpression(context: Map<String, Any>): String = iterator().parseExpression(context)


fun Iterable<Char>.parseExpression(context: ParameterProvider): String = iterator().parseExpression(context)
fun CharSequence.parseExpression(context: ParameterProvider): String = iterator().parseExpression(context)
