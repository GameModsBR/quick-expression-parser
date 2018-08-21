package br.com.gamemods.qep

internal open class SingleRollbackIterator<T : Any>(protected val input: Iterator<T>) : Iterator<T> {
    private lateinit var read: T
    private var rollback = false

    override fun hasNext(): Boolean {
        return rollback || input.hasNext()
    }

    override fun next(): T {
        return if (rollback) read.also { rollback = false }
        else input.next().also { read = it }
    }

    open fun rollback() {
        check(!rollback)
        rollback = true
    }
}