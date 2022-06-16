package br.com.gamemods.qep

import br.com.gamemods.qep.jvm.ReflectionParameterProvider
import org.openjdk.jmh.annotations.Benchmark
import org.openjdk.jmh.annotations.BenchmarkMode
import org.openjdk.jmh.annotations.Scope
import org.openjdk.jmh.annotations.State

@BenchmarkMode
class JMHTests {
    @Benchmark
    fun simpleRead() {
        "Teste: #a #b #c".parseExpression("a" to 1, "c" to 2)
    }

    @Benchmark
    fun simpleExpression() {
        "a #{b} c".parseExpression("b" to 1)
    }

    @Benchmark
    fun emptySubValue() {
        "a #{b.c} d".parseExpression()
    }

    @Benchmark
    fun filledSubValue() {
        "a #{b.c} d".parseExpression(
                "b" to mapOf(
                        "c" to 1
                )
        )
    }

    @Benchmark
    fun filledSubValueProvider(player: Player) {
        "a #{player.name} d".parseExpression(
                "player" to player
        )
    }

    @State(Scope.Benchmark)
    class Player: ParameterProvider {
        override fun getParameter(identifier: String): Any? {
            return when (identifier) {
                "name" -> "joserobjr"
                else -> null
            }
        }
    }

    @Benchmark
    fun filledSubValueReflection(reflectivePlayer: ReflectivePlayer) {
        "a #{player.name} d".parseExpression(
                "player" to reflectivePlayer
        )
    }

    @State(Scope.Benchmark)
    class ReflectivePlayer: ReflectionParameterProvider {
        val name = "joserobjr"
        val isMale = true
    }
}
