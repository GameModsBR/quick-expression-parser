package br.com.gamemods.qep

interface ParameterProvider {
    fun getParameter(identifier: String): Any?
    fun value(): String? = null
}

internal object NoParameters : ParameterProvider {
    override fun getParameter(identifier: String): Nothing? = null
    override fun value(): Nothing? = null
}

internal class MapParameterProvider(private val map: Map<String, Any>): ParameterProvider {
    override fun getParameter(identifier: String) = map[identifier]
    override fun value() = map["value"]?.toString()
}

internal class ExpressionContext {
    private val parameterProvider: ParameterProvider
    private val parameters = mutableMapOf<String, Parameter>()

    constructor() {
        parameterProvider = NoParameters
    }

    constructor(provider: ParameterProvider) {
        parameterProvider = provider
    }

    constructor(context: Map<String, Any>) {
        parameterProvider = MapParameterProvider(context)
    }

    internal fun consume(identifier: String): String? {
        return provide(identifier)?.value
    }

    internal fun provide(identifier: String): Parameter? {
        return parameters.getOrPut(identifier) {
            val param = parameterProvider.getParameter(identifier) ?: return null
            when (param) {
                is ParameterProvider -> ProvidedParameter(param)
                is Map<*, *> -> MapParameter(param)
                else -> ProcessedParameter(param.toString())
            }
        }
    }

    companion object {
        internal val EMPTY = ExpressionContext()
    }
}
