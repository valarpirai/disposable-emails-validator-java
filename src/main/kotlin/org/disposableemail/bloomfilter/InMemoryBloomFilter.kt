package org.disposableemail.bloomfilter

import org.apache.commons.codec.digest.MurmurHash3
import kotlin.math.abs
import kotlin.math.ln
import kotlin.math.pow

class InMemoryBloomFilter(
    override val data: LongArray?, override val expectedInsertionCount: Int, override val falsePositivePercentage: Double
) : BloomFilter<String> {

    private var totalItems = 0
    private val bitSize = optimalBitSize()
    private val hashCount = hashFunctionCount()
    private val bucket: BitArray =
        if (data != null)
            BitArray(data)
        else
            BitArray(bitSize)

    private fun optimalBitSize(): Int {
        return (-(expectedInsertionCount * ln(falsePositivePercentage) / ln(2.0).pow(2))).toInt()
    }

    private fun hashFunctionCount(): Int {
        return ((bitSize / expectedInsertionCount) * ln(2.0)).toInt()
    }

    override fun add(value: String): Boolean {
        this.totalItems++
        for (i in 1..hashCount) {
            val index = abs(MurmurHash3.hash32x86(value.toByteArray(), 0, value.length, i) % this.bitSize)
            bucket.set(index)
        }
        return true
    }

    override fun addAll(values: Collection<String>?): Boolean {
        if (values != null) {
            for (value in values) {
                add(value)
            }
        }
        return true
    }

    override fun contains(value: String): Boolean {
        for (i in 1..hashCount) {
            val index = abs(MurmurHash3.hash32x86(value.toByteArray(), 0, value.length, i) % this.bitSize)
            if (!bucket.get(index))
                return false
        }
        return true
    }

    // TODO: Calculate
    override fun getFalsePositiveProbability(numInsertedElements: Int): Double {
        return 0.0
    }

    override fun getInsertedItemsCount(): Int {
        return totalItems
    }
}
