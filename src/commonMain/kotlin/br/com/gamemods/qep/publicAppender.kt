@file:JvmName("QuickExpressionParser")
@file:JvmMultifileClass
@file:Suppress("unused")

package br.com.gamemods.qep

import java.util.*

/**
 * Uses a given map as context to parse a string. The map may have sub-maps to create an object style context.
 * @sample examples.qep.parseExpressionMapSample
 */
fun Iterator<Char>.parseExpression(context: Map<String, Any>, locale: Locale, appender: (Any?) -> Unit) {
    readRaw(null, SingleRollbackIterator(this), ExpressionContext(context, locale), appender)
}

/**
 * Uses a given map as context to parse a string. The map may have sub-maps to create an object style context.
 * @sample examples.qep.parseExpressionMapSample
 */
fun Iterator<Char>.parseExpression(context: Map<String, Any>, appender: (Any?) -> Unit): Unit = parseExpression(context, Locale.getDefault(), appender)

/**
 * Uses a given parameter provider as context to parse a string.
 * @sample examples.qep.parseExpressionParamProviderSample
 */
fun Iterator<Char>.parseExpression(context: ParameterProvider, appender: (Any?) -> Unit): Unit = parseExpression(context, Locale.getDefault(), appender)

/**
 * Uses a given parameter provider as context to parse a string.
 * @sample examples.qep.parseExpressionParamProviderSample
 */
fun Iterator<Char>.parseExpression(context: ParameterProvider, locale: Locale, appender: (Any?) -> Unit) {
    readRaw(null, SingleRollbackIterator(this), ExpressionContext(context, locale), appender)
}

/**
 * Uses a given map as context to parse a string. The map may have sub-maps to create an object style context.
 * @sample examples.qep.parseExpressionVarargSample
 */
fun Iterator<Char>.parseExpression(vararg context: Pair<String, Any>, appender: (Any?) -> Unit): Unit = parseExpression(Locale.getDefault(), *context, appender = appender)

/**
 * Uses a given map as context to parse a string. The map may have sub-maps to create an object style context.
 * @sample examples.qep.parseExpressionVarargSample
 */
fun Iterator<Char>.parseExpression(locale: Locale, vararg context: Pair<String, Any>, appender: (Any?) -> Unit): Unit = parseExpression(context.toMap(), locale, appender = appender)

/**
 * Uses a given map as context to parse a string. The map may have sub-maps to create an object style context.
 * @sample examples.qep.parseExpressionVarargSample
 */
fun Iterable<Char>.parseExpression(vararg context: Pair<String, Any>, appender: (Any?) -> Unit): Unit = parseExpression(Locale.getDefault(), *context, appender = appender)

/**
 * Uses a given map as context to parse a string. The map may have sub-maps to create an object style context.
 * @sample examples.qep.parseExpressionVarargSample
 */
fun Iterable<Char>.parseExpression(locale: Locale, vararg context: Pair<String, Any>, appender: (Any?) -> Unit): Unit
        = iterator().parseExpression(context.toMap(), locale, appender = appender)

/**
 * Uses a given map as context to parse a string. The map may have sub-maps to create an object style context.
 * @sample examples.qep.parseExpressionVarargSample
 */
fun CharSequence.parseExpression(vararg context: Pair<String, Any>, appender: (Any?) -> Unit): Unit = parseExpression(Locale.getDefault(), *context, appender = appender)

/**
 * Uses a given map as context to parse a string. The map may have sub-maps to create an object style context.
 * @sample examples.qep.parseExpressionVarargSample
 */
fun CharSequence.parseExpression(locale: Locale, vararg context: Pair<String, Any>, appender: (Any?) -> Unit): Unit
        = iterator().parseExpression(context.toMap(), locale, appender = appender)


/**
 * Uses a given map as context to parse a string. The map may have sub-maps to create an object style context.
 * @sample examples.qep.parseExpressionMapSample
 */
fun Iterable<Char>.parseExpression(context: Map<String, Any>, appender: (Any?) -> Unit): Unit = iterator().parseExpression(context, Locale.getDefault(), appender = appender)

/**
 * Uses a given map as context to parse a string. The map may have sub-maps to create an object style context.
 * @sample examples.qep.parseExpressionMapSample
 */
fun Iterable<Char>.parseExpression(context: Map<String, Any>, locale: Locale, appender: (Any?) -> Unit): Unit = iterator().parseExpression(context, locale, appender = appender)

/**
 * Uses a given map as context to parse a string. The map may have sub-maps to create an object style context.
 * @sample examples.qep.parseExpressionMapSample
 */
fun CharSequence.parseExpression(context: Map<String, Any>, appender: (Any?) -> Unit): Unit = iterator().parseExpression(context, Locale.getDefault(), appender = appender)

/**
 * Uses a given map as context to parse a string. The map may have sub-maps to create an object style context.
 * @sample examples.qep.parseExpressionMapSample
 */
fun CharSequence.parseExpression(context: Map<String, Any>, locale: Locale, appender: (Any?) -> Unit): Unit = iterator().parseExpression(context, locale, appender = appender)

/**
 * Uses a given parameter provider as context to parse a string.
 * @sample examples.qep.parseExpressionMapSample
 */
fun Iterable<Char>.parseExpression(context: ParameterProvider, appender: (Any?) -> Unit): Unit = iterator().parseExpression(context, Locale.getDefault(), appender = appender)

/**
 * Uses a given parameter provider as context to parse a string.
 * @sample examples.qep.parseExpressionMapSample
 */
fun Iterable<Char>.parseExpression(context: ParameterProvider, locale: Locale, appender: (Any?) -> Unit): Unit = iterator().parseExpression(context, locale, appender = appender)

/**
 * Uses a given parameter provider as context to parse a string.
 * @sample examples.qep.parseExpressionMapSample
 */
fun CharSequence.parseExpression(context: ParameterProvider, appender: (Any?) -> Unit): Unit = iterator().parseExpression(context, Locale.getDefault(), appender = appender)

/**
 * Uses a given parameter provider as context to parse a string.
 * @sample examples.qep.parseExpressionMapSample
 */
fun CharSequence.parseExpression(context: ParameterProvider, locale: Locale, appender: (Any?) -> Unit): Unit = iterator().parseExpression(context, locale, appender = appender)
