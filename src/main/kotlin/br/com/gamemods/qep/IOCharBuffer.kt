package br.com.gamemods.qep

internal open class IOCharBuffer : Iterator<Char> {
    private var buffer = CharArray(32)
    private var write = 0
    private var read = 0

    fun write(char: Char) {
        if (write == buffer.size) {
            buffer = buffer.copyOf(buffer.size + 32)
        }
        buffer[write++] = char
    }

    override fun hasNext() = read < write
    override fun next() = buffer[read++].also {
        if (read == write) {
            read = 0
            write = 0
        }
    }

    fun readAll() = buffer.copyOf(read).contentToString().also {
        read = 0
        write = 0
    }
}