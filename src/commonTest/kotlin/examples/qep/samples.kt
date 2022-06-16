@file:Suppress("UndocumentedPublicClass", "unused")

package examples.qep

import br.com.gamemods.qep.ParameterProvider
import br.com.gamemods.qep.parseExpression

private fun parseExpressionMapSample()
        = "Hello #{user.name}, today is #dayOfWeek. Will you go #{user.isAdult? 'work' : 'to school'} today?"
        .parseExpression(mapOf(
                "dayOfWeek" to "monday",
                "user" to mapOf(
                        "name" to "Michael",
                        "isAdult" to true
                )
        ))

private fun parseExpressionVarargSample()
        = "Hello #{user.name}, today is #dayOfWeek. Will you go #{user.isAdult? 'work' : 'to school'} today?"
        .parseExpression(
                "dayOfWeek" to "monday",
                "user" to mapOf(
                        "name" to "Michael",
                        "isAdult" to true
                )
        )
private fun parseExpressionParamProviderSample(): String {
    data class UserBean(val name: String, val age: Int): ParameterProvider {
        override fun getParameter(identifier: String): Any? = when (identifier) {
            "name" -> name
            "isAdult" -> age >= 18
            else -> null
        }
    }

    data class ExampleBean(val dayOfWeek: String, val user: UserBean): ParameterProvider {
        override fun getParameter(identifier: String): Any? = when (identifier) {
            "dayOfWeek" -> dayOfWeek
            "user" -> user
            else -> null
        }
    }

    val bean = ExampleBean("monday", UserBean("Michael", 12))

    return "Hello #{user.name}, today is #dayOfWeek. Will you go #{user.isAdult? 'work' : 'to school'} today?"
            .parseExpression(bean)
}
