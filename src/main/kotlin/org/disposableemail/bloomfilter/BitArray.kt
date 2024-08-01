package org.disposableemail.bloomfilter

class BitArray(data: LongArray?) {
    /**
     * The bit storage
     */
    var data: LongArray

    /**
     * The current enabled/set bit count
     */
    private var setBitCount: Int

    /**
     * Construct an instance of the [BitArray] that can hold
     * the given number of bits
     *
     * @param bits number of bits
     */
    constructor(bits: Int) : this(LongArray(divide(bits, Long.SIZE_BITS)))

    // Used by serialization
    init {
        require(!(data == null || data.isEmpty())) { "Data is either null or empty" }
        this.data = data
        var bitCount = 0
        for (value in data) {
            bitCount += java.lang.Long.bitCount(value)
        }

        this.setBitCount = bitCount
    }

    /** Returns true if the bit changed value.  */
    fun set(index: Int): Boolean {
        if (!get(index)) {
            data[index shr 6] = data[index shr 6] or (1L shl index)
            setBitCount++
            return true
        }

        return false
    }

    fun get(index: Int): Boolean {
        return (data[index shr 6] and (1L shl index)) != 0L
    }

    /**
     * Number of bits
     *
     * @return total number of bits allocated
     */
    fun bitSize(): Int {
        return data.size * java.lang.Long.SIZE
    }

    /**
     * Number of set bits (1s)
     *
     * @return the number of set bits
     */
    fun bitCount(): Int {
        return this.setBitCount
    }

    /**
     * Copy the bitset to new bitset
     *
     * @return a new [BitArray] that is exactly in the same state as
     * this
     */
    fun copy(): BitArray {
        return BitArray(data.clone())
    }

    override fun equals(other: Any?): Boolean {
        if (other is BitArray) {
            return data.contentEquals(other.data)
        }
        return false
    }

    override fun hashCode(): Int {
        return data.contentHashCode()
    }

    companion object {
        /**
         * Returns the size for the data array
         *
         */
        fun divide(x: Int, y: Int): Int {
            val div = x / y
            val rem = x % y

            return if (rem == 0) {
                div
            } else {
                div + 1
            }
        }
    }
}
