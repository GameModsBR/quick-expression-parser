package br.com.gamemods.qep

internal abstract class Parameter(val context: ExpressionContext? = null) {
    abstract val value: String
}

internal class ProcessedParameter(override val value: String, context: ExpressionContext? = null): Parameter(context)

internal class ProvidedParameter(private val provider: ParameterProvider): Parameter(ExpressionContext(provider)) {
    override val value: String by lazy { provider.value() ?: "#!value?" }
}

@Suppress("UNCHECKED_CAST")
internal class MapParameter(map: Map<*,*>): Parameter(ExpressionContext(map as Map<String, Any>)) {
    override val value: String by lazy { context?.consume("value") ?: "#!value?" }
}
