package br.com.gamemods.qep.jvm

import br.com.gamemods.qep.ParameterProvider
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1
import kotlin.reflect.KVisibility
import kotlin.reflect.full.memberProperties

internal class MaxSizeHashMap<K, V>(private val maxSize: Int) : LinkedHashMap<K, V>() {
    override fun removeEldestEntry(eldest: MutableMap.MutableEntry<K, V>?): Boolean {
        return size > maxSize
    }
}

//private val caches = MaxSizeHashMap<String, KProperty1<*, *>>(100)
//private val caches = hashMapOf<String, KProperty1<*, *>?>()

/**
 * Expõe todas as propriedades públicas da implementação desta interface.
 *
 * **Atenção! Este provider é cerca de 150x mais lento que as alternativas sem reflection!**
 */
interface ReflectionParameterProvider : ParameterProvider {
    override fun getParameter(identifier: String): Any? {
        val kClass = this::class
        /*val key = "$kClass - $identifier"
        if (key in caches) {
            @Suppress("UNCHECKED_CAST")
            return (caches[key] as? KProperty1<ReflectionParameterProvider, *>)?.get(this)
        }
*/
        val prop = kClass.memberProperties.find {
            it.visibility == KVisibility.PUBLIC && it.name == identifier }

//        caches[key] = prop

        @Suppress("UNCHECKED_CAST")
        return (prop as? KProperty1<ReflectionParameterProvider, *>)?.get(this)
    }
}

inline fun <reified T: Any>  T.asParameterProvider(): ParameterProvider {
    return ProxiedReflectionParameterProvider(this, T::class)
}

/**
 * Expõe todas as propriedades públicas da implementação desta interface.
 *
 * **Atenção! Este provider é cerca de 150x mais lento que as alternativas sem reflection!**
 */
class ProxiedReflectionParameterProvider<T: Any>(private val target: T, private val tClass: KClass<T>) : ParameterProvider {
    override fun getParameter(identifier: String): Any? {
        /*val key = "$tClass - $identifier"
        if (key in caches) {
            @Suppress("UNCHECKED_CAST")
            return (caches[key] as? KProperty1<T, *>)?.get(target)
        }*/

        val prop = tClass.memberProperties.find {
            it.visibility == KVisibility.PUBLIC && it.name == identifier }

        /*caches[key] = prop*/

        return prop?.get(target)
    }
}
