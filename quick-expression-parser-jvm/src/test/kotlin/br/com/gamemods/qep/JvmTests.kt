package br.com.gamemods.qep

import br.com.gamemods.qep.jvm.ReflectionParameterProvider
import org.junit.Test
import kotlin.test.assertEquals

class JvmTests {
    @Test
    fun filledSubValueReflection() {
        assertEquals("a joserobjr d", "a #{player.name} d".parseExpression(
                "player" to ReflectivePlayer()
        ))
    }

    data class ReflectivePlayer(
            var name: String = "joserobjr",
            var isMale: Boolean = true,
            var isFemale: Boolean = false
    ): ReflectionParameterProvider

    private val expr = "acha que #{player.name} é #{player.isFemale ? 'uma sabotadora' : player.isMale ? 'um sabotador' : 'um(a) sabotador(a)'}!"

    @Test
    fun ifConditionWithReflectionM() {
        assertEquals("acha que joserobjr é um sabotador!", expr.parseExpression(
                "player" to ReflectivePlayer()
        ))
    }

    @Test
    fun ifConditionWithReflectionF() {
        assertEquals("acha que Helelinha1 é uma sabotadora!", expr.parseExpression(
                "player" to ReflectivePlayer("Helelinha1", false, true)
        ))
    }

    @Test
    fun ifConditionWithReflectionU() {
        assertEquals("acha que joserobjr é um(a) sabotador(a)!", expr.parseExpression(
                "player" to ReflectivePlayer("joserobjr", false, false)
        ))
    }
}
