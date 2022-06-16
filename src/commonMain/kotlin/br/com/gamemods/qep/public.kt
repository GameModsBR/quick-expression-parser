@file:JvmName("QuickExpressionParser")
@file:JvmMultifileClass
@file:Suppress("unused")

package br.com.gamemods.qep

import java.util.*

/**
 * Uses a given map as context to parse a string. The map may have sub-maps to create an object style context.
 * @sample examples.qep.parseExpressionMapSample
 */
fun Iterator<Char>.parseExpression(context: Map<String, Any>, locale: Locale): String {
    return StringBuilder().also {
        readRaw(null, SingleRollbackIterator(this), it, ExpressionContext(context, locale))
    }.toString()
}

/**
 * Uses a given map as context to parse a string. The map may have sub-maps to create an object style context.
 * @sample examples.qep.parseExpressionMapSample
 */
fun Iterator<Char>.parseExpression(context: Map<String, Any>): String = parseExpression(context, Locale.getDefault())

/**
 * Uses a given parameter provider as context to parse a string.
 * @sample examples.qep.parseExpressionParamProviderSample
 */
fun Iterator<Char>.parseExpression(context: ParameterProvider): String = parseExpression(context, Locale.getDefault())

/**
 * Uses a given parameter provider as context to parse a string.
 * @sample examples.qep.parseExpressionParamProviderSample
 */
fun Iterator<Char>.parseExpression(context: ParameterProvider, locale: Locale): String {
    return StringBuilder().also {
        readRaw(null, SingleRollbackIterator(this), it, ExpressionContext(context, locale))
    }.toString()
}

/**
 * Uses a given map as context to parse a string. The map may have sub-maps to create an object style context.
 * @sample examples.qep.parseExpressionVarargSample
 */
fun Iterator<Char>.parseExpression(vararg context: Pair<String, Any>): String = parseExpression(Locale.getDefault(), *context)

/**
 * Uses a given map as context to parse a string. The map may have sub-maps to create an object style context.
 * @sample examples.qep.parseExpressionVarargSample
 */
fun Iterator<Char>.parseExpression(locale: Locale, vararg context: Pair<String, Any>): String = parseExpression(context.toMap(), locale)

/**
 * Uses a given map as context to parse a string. The map may have sub-maps to create an object style context.
 * @sample examples.qep.parseExpressionVarargSample
 */
fun Iterable<Char>.parseExpression(vararg context: Pair<String, Any>): String = parseExpression(Locale.getDefault(), *context)

/**
 * Uses a given map as context to parse a string. The map may have sub-maps to create an object style context.
 * @sample examples.qep.parseExpressionVarargSample
 */
fun Iterable<Char>.parseExpression(locale: Locale, vararg context: Pair<String, Any>): String
        = iterator().parseExpression(context.toMap(), locale)

/**
 * Uses a given map as context to parse a string. The map may have sub-maps to create an object style context.
 * @sample examples.qep.parseExpressionVarargSample
 */
fun CharSequence.parseExpression(vararg context: Pair<String, Any>): String = parseExpression(Locale.getDefault(), *context)

/**
 * Uses a given map as context to parse a string. The map may have sub-maps to create an object style context.
 * @sample examples.qep.parseExpressionVarargSample
 */
fun CharSequence.parseExpression(locale: Locale, vararg context: Pair<String, Any>): String
        = iterator().parseExpression(context.toMap(), locale)


/**
 * Uses a given map as context to parse a string. The map may have sub-maps to create an object style context.
 * @sample examples.qep.parseExpressionMapSample
 */
fun Iterable<Char>.parseExpression(context: Map<String, Any>): String = iterator().parseExpression(context, Locale.getDefault())

/**
 * Uses a given map as context to parse a string. The map may have sub-maps to create an object style context.
 * @sample examples.qep.parseExpressionMapSample
 */
fun Iterable<Char>.parseExpression(context: Map<String, Any>, locale: Locale): String = iterator().parseExpression(context, locale)

/**
 * Uses a given map as context to parse a string. The map may have sub-maps to create an object style context.
 * @sample examples.qep.parseExpressionMapSample
 */
fun CharSequence.parseExpression(context: Map<String, Any>): String = iterator().parseExpression(context, Locale.getDefault())

/**
 * Uses a given map as context to parse a string. The map may have sub-maps to create an object style context.
 * @sample examples.qep.parseExpressionMapSample
 */
fun CharSequence.parseExpression(context: Map<String, Any>, locale: Locale): String = iterator().parseExpression(context, locale)

/**
 * Uses a given parameter provider as context to parse a string.
 * @sample examples.qep.parseExpressionMapSample
 */
fun Iterable<Char>.parseExpression(context: ParameterProvider): String = iterator().parseExpression(context, Locale.getDefault())

/**
 * Uses a given parameter provider as context to parse a string.
 * @sample examples.qep.parseExpressionMapSample
 */
fun Iterable<Char>.parseExpression(context: ParameterProvider, locale: Locale): String = iterator().parseExpression(context, locale)

/**
 * Uses a given parameter provider as context to parse a string.
 * @sample examples.qep.parseExpressionMapSample
 */
fun CharSequence.parseExpression(context: ParameterProvider): String = iterator().parseExpression(context, Locale.getDefault())

/**
 * Uses a given parameter provider as context to parse a string.
 * @sample examples.qep.parseExpressionMapSample
 */
fun CharSequence.parseExpression(context: ParameterProvider, locale: Locale): String = iterator().parseExpression(context, locale)
