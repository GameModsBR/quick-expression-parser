package br.com.gamemods.qep

import java.util.*

/**
 * Provides information about parameters dynamically.
 */
interface ParameterProvider {
    /**
     * Optionally returns the value from a given identifier.
     * @return ´null´ if it was not found or not supported.
     */
    fun getParameter(identifier: String): Any?

    /**
     * The String which is appended when this object is used directly without parameters.
     * @return ´null´ if the default behavior must be applied.
     */
    fun value(): Any? = null
}

interface LocalizedParameterProvider: ParameterProvider {
    /**
     * Optionally returns the value from a given identifier for a given locale.
     * @return ´null´ if it was not found or not supported.
     */
    fun getParameter(identifier: String, locale: Locale): Any?

    override fun getParameter(identifier: String): Any? = getParameter(identifier, Locale.getDefault())

    /**
     * The String which is appended when this object is used directly without parameters.
     * @return ´null´ if the default behavior must be applied.
     */
    fun value(locale: Locale): Any? = null

    override fun value(): Any? {
        return value(Locale.getDefault())
    }
}

internal object NoParameters : ParameterProvider {
    override fun getParameter(identifier: String): Nothing? = null
    override fun value(): Any? = null
}

internal class MapParameterProvider(private val map: Map<String, Any>): ParameterProvider {
    override fun getParameter(identifier: String) = map[identifier]
    override fun value(): Any? = map["value"]?.toString()
}

internal class ExpressionContext {
    private val parameterProvider: ParameterProvider
    private val parameters = mutableMapOf<String, Parameter>()
    internal val locale: Locale

    constructor() {
        parameterProvider = NoParameters
        locale = Locale.getDefault()
    }

    constructor(provider: ParameterProvider, locale: Locale) {
        parameterProvider = provider
        this.locale = locale
    }

    constructor(context: Map<String, Any>, locale: Locale) {
        parameterProvider = MapParameterProvider(context)
        this.locale = locale
    }

    internal fun consume(identifier: String): Any? {
        return provide(identifier)?.value(locale)
    }

    internal fun provide(identifier: String): Parameter? {
        return parameters.getOrPut(identifier) {
            val param = parameterProvider.getParameter(identifier) ?: return null
            when (param) {
                is ParameterProvider -> ProvidedParameter(param, locale)
                is Map<*, *> -> MapParameter(param, locale)
                else -> ProcessedParameter(param)
            }
        }
    }

    companion object {
        internal val EMPTY = ExpressionContext()
    }
}
