package br.com.gamemods.qep

internal class ReadChain(private val input: Iterator<Char>) : CharStack() {
    override fun hasNext(): Boolean {
        return super.hasNext() || input.hasNext()
    }

    override fun next(): Char {
        return if (super.hasNext()) {
            super.next()
        } else {
            input.next()
        }
    }
}