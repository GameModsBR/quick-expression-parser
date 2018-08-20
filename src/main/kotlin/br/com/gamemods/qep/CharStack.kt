package br.com.gamemods.qep

open class CharStack: Iterator<Char> {
    private var stack = CharArray(16)
    private var size = 0

    operator fun plusAssign(char: Char) {
        if (size == stack.size) {
            stack = stack.copyOf(stack.size + 16)
        }
        stack[size++] = char
    }

    override fun hasNext(): Boolean {
        return size > 0
    }

    override fun next(): Char {
        if (size == 0) {
            throw NoSuchElementException()
        }

        return stack[size--]
    }
}