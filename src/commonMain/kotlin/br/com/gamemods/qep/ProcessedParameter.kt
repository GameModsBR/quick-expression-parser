package br.com.gamemods.qep

import java.util.*

internal abstract class Parameter(val context: ExpressionContext? = null) {
    abstract fun value(locale: Locale): Any
}

internal class ProcessedParameter(private val value: Any, context: ExpressionContext? = null): Parameter(context) {
    override fun value(locale: Locale): Any = value
}

internal class ProvidedParameter(private val provider: ParameterProvider, locale: Locale): Parameter(ExpressionContext(provider, locale)) {
    override fun value(locale: Locale): Any {
        val value = if (provider is LocalizedParameterProvider) provider.value(locale)
        else provider.value()
        return value ?: "#!value?"
    }
}

@Suppress("UNCHECKED_CAST")
internal class MapParameter(map: Map<*,*>, locale: Locale): Parameter(ExpressionContext(map as Map<String, Any>, locale)) {
    override fun value(locale: Locale): Any = context?.consume("value") ?: "#!value?"
}
