package org.disposable.bloomfilter

import java.nio.charset.Charset

interface BloomFilter<T> {
    /**
     * Add the given value object to the bloom filter
     *
     * @param value
     * the object to be added to the bloom filter
     *
     * @return `true` if any bit was modified when adding the value,
     * `false` otherwise
     */
    fun add(value: T): Boolean

    /**
     * Add all the values represented as a collection of objects to the bloom
     * filter.
     *
     * @param values
     * the values to be added to the bloom filter
     *
     * @return `true` if any bit was modified when adding the values,
     * `false` otherwise
     */
    fun addAll(values: Collection<T>?): Boolean

    /**
     * Check if the value object is present in the bloom filter or not
     *
     * @param value
     * the object to be tested for existence in bloom filter
     *
     * @return `true` if the bloom filter indicates the presence of
     * entry, `false` otherwise
     */
    fun contains(value: T): Boolean


    val expectedInsertionCount: Int

    val falsePositivePercentage: Double

    /**
     * Estimate the current false positive rate (approximated) when given number
     * of elements have been inserted in to the filter.
     *
     * @param numInsertedElements
     * the number of elements inserted into the filter
     *
     * @return the approximated false positive rate
     */
    fun getFalsePositiveProbability(numInsertedElements: Int): Double

    fun getInsertedItemsCount(): Int
}
