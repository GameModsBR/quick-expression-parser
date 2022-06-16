package br.com.gamemods.qep

import kotlin.test.Test
import kotlin.test.assertEquals

class Tests {
    @Test
    fun simpleRead() {
        assertEquals("Teste: 1 #b 2", "Teste: #a #b #c".parseExpression(
                "a" to 1, "c" to 2))
    }

    @Test
    fun simpleExpression() {
        assertEquals("a 1 c", "a #{b} c".parseExpression("b" to 1))
    }

    @Test
    fun emptySubValue() {
        assertEquals("a #{b.c} d", "a #{b.c} d".parseExpression())
    }

    @Test
    fun filledSubValue() {
        assertEquals("a 1 d", "a #{b.c} d".parseExpression(
                "b" to mapOf(
                        "c" to 1
                )
        ))
    }

    object Player: ParameterProvider {
        override fun getParameter(identifier: String): Any? {
            return when (identifier) {
                "name" -> "joserobjr"
                "isMale" -> true
                "isFemale" -> false
                else -> null
            }
        }
    }

    @Test
    fun filledSubValueProvider() {
        assertEquals("a joserobjr d", "a #{player.name} d".parseExpression(
                "player" to Player
        ))
    }

    private val expr = "acha que #{player.name} é #{player.isFemale ? 'uma sabotadora' : player.isMale ? 'um sabotador' : 'um(a) sabotador(a)'}!"

    @Test
    fun ifConditionWithProvider() {
        assertEquals("acha que joserobjr é um sabotador!", expr.parseExpression("player" to Player))
    }
}