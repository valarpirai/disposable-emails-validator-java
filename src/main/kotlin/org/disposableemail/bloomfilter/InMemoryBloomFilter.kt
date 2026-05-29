package org.disposableemail.bloomfilter

import org.apache.commons.codec.digest.MurmurHash3
import kotlin.math.ln
import kotlin.math.pow

class InMemoryBloomFilter(
    serialisedData: LongArray?,
    val expectedInsertionCount: Int,
    val falsePositivePercentage: Double
) : BloomFilter<String> {

    private var totalItems = 0

    // Always derive bitSize from the formula so the modulus is identical whether we
    // are building fresh or restoring from serialised data. The BitArray may have a
    // few extra unused bits at the end, which is safe because indices are always < bitSize.
    private val bitSize: Int = optimalBitSize()
    private val hashCount: Int = hashFunctionCount()
    private val bucket: BitArray = if (serialisedData != null) BitArray(serialisedData) else BitArray(bitSize)

    override val data: LongArray get() = bucket.data

    private fun optimalBitSize(): Int =
        (-(expectedInsertionCount * ln(falsePositivePercentage) / ln(2.0).pow(2))).toInt()

    private fun hashFunctionCount(): Int =
        ((bitSize.toDouble() / expectedInsertionCount) * ln(2.0)).toInt()

    override fun add(value: String): Boolean {
        totalItems++
        val bytes = value.toByteArray()
        for (i in 1..hashCount) {
            val index = hashIndex(bytes, i)
            bucket.set(index)
        }
        return true
    }

    override fun addAll(values: Collection<String>?): Boolean {
        values?.forEach { add(it) }
        return true
    }

    override fun contains(value: String): Boolean {
        val bytes = value.toByteArray()
        for (i in 1..hashCount) {
            if (!bucket.get(hashIndex(bytes, i))) return false
        }
        return true
    }

    // Casting to Long before abs() avoids the abs(Int.MIN_VALUE) overflow while
    // keeping the same index formula the serialised resource file was built with.
    private fun hashIndex(bytes: ByteArray, seed: Int): Int =
        (Math.abs(MurmurHash3.hash32x86(bytes, 0, bytes.size, seed).toLong()) % bitSize).toInt()

    override fun getFalsePositiveProbability(numInsertedElements: Int): Double {
        val exponent = -hashCount.toDouble() * numInsertedElements / bitSize
        return Math.pow(1 - Math.exp(exponent), hashCount.toDouble())
    }

    override fun getInsertedItemsCount(): Int = totalItems
}
