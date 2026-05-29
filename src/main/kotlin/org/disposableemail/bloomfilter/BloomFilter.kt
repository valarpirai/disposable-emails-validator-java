package org.disposableemail.bloomfilter

interface BloomFilter<T> {
    fun add(value: T): Boolean
    fun addAll(values: Collection<T>?): Boolean
    fun contains(value: T): Boolean
    fun getInsertedItemsCount(): Int
    fun getFalsePositiveProbability(numInsertedElements: Int): Double

    /** Raw bit array for serialisation. */
    val data: LongArray
}
