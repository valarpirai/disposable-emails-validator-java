package org.disposableemail.bloomfilter

interface BloomFilter<T> {
    /**
     * Add value to the bloom filter
     */
    fun add(value: T): Boolean

    /**
     * Add all the values from the collection
     */
    fun addAll(values: Collection<T>?): Boolean

    /**
     * Check if the value object is present in the bloom filter or not
     */
    fun contains(value: T): Boolean

    val data: LongArray?

    val expectedInsertionCount: Int

    val falsePositivePercentage: Double

    fun getFalsePositiveProbability(numInsertedElements: Int): Double

    fun getInsertedItemsCount(): Int
}
