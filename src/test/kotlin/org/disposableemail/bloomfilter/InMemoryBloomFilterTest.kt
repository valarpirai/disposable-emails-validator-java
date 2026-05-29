package org.disposableemail.bloomfilter

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class InMemoryBloomFilterTest {

    private fun newFilter(
        expected: Int = 1_000,
        fpp: Double = 0.01
    ) = InMemoryBloomFilter(null, expected, fpp)

    // ── Basic correctness ────────────────────────────────────────────────────

    @Test
    fun `contains returns false for empty filter`() {
        val filter = newFilter()
        assertFalse(filter.contains("gmail.com"))
        assertFalse(filter.contains("yopmail.com"))
    }

    @Test
    fun `contains returns true for added value`() {
        val filter = newFilter()
        filter.add("mailsac.com")
        assertTrue(filter.contains("mailsac.com"))
    }

    @Test
    fun `contains is case-sensitive`() {
        val filter = newFilter()
        filter.add("mailsac.com")
        assertFalse(filter.contains("MAILSAC.COM"))
        assertFalse(filter.contains("Mailsac.Com"))
    }

    @Test
    fun `add multiple distinct values and all are found`() {
        val filter = newFilter()
        val domains = listOf("yopmail.com", "mailsac.com", "guerrillamail.com", "trashmail.com")
        domains.forEach { filter.add(it) }
        domains.forEach { assertTrue(filter.contains(it), "Expected $it to be present") }
    }

    @Test
    fun `addAll inserts all values from collection`() {
        val filter = newFilter()
        val domains = listOf("a.com", "b.com", "c.com")
        filter.addAll(domains)
        domains.forEach { assertTrue(filter.contains(it)) }
    }

    @Test
    fun `addAll with null collection does not throw`() {
        val filter = newFilter()
        assertDoesNotThrow { filter.addAll(null) }
    }

    @Test
    fun `getInsertedItemsCount tracks every add call`() {
        val filter = newFilter()
        assertEquals(0, filter.getInsertedItemsCount())
        filter.add("a.com")
        assertEquals(1, filter.getInsertedItemsCount())
        filter.add("a.com") // duplicate — still counted
        assertEquals(2, filter.getInsertedItemsCount())
        filter.add("b.com")
        assertEquals(3, filter.getInsertedItemsCount())
    }

    // ── False positive rate ──────────────────────────────────────────────────

    @Test
    fun `false positive rate stays within 2x configured rate`() {
        val expectedInsertions = 10_000
        val targetFpp = 0.01
        val filter = InMemoryBloomFilter(null, expectedInsertions, targetFpp)

        // Insert known domains
        (1..expectedInsertions).forEach { filter.add("domain$it.com") }

        // Check unseen domains for false positives
        val probes = 10_000
        val falsePositives = (expectedInsertions + 1..expectedInsertions + probes)
            .count { filter.contains("domain$it.com") }

        val observedFpp = falsePositives.toDouble() / probes
        assertTrue(observedFpp < targetFpp * 2,
            "FPP $observedFpp exceeded 2× target of $targetFpp")
    }

    @Test
    fun `no false negatives — added values are always found`() {
        val filter = InMemoryBloomFilter(null, 5_000, 0.01)
        val domains = (1..5_000).map { "added-domain$it.com" }
        domains.forEach { filter.add(it) }
        domains.forEach { assertTrue(filter.contains(it), "False negative for $it") }
    }

    // ── Serialisation round-trip ─────────────────────────────────────────────

    @Test
    fun `filter serialised to LongArray and restored produces same results`() {
        val original = InMemoryBloomFilter(null, 1_000, 0.01)
        val inserted = listOf("yopmail.com", "mailsac.com", "trashmail.com")
        inserted.forEach { original.add(it) }

        val restored = InMemoryBloomFilter(original.data, 1_000, 0.01)
        inserted.forEach { assertTrue(restored.contains(it), "Restored filter missed $it") }
    }

    // ── Edge cases ───────────────────────────────────────────────────────────

    @Test
    fun `empty string can be added and found`() {
        val filter = newFilter()
        filter.add("")
        assertTrue(filter.contains(""))
    }

    @Test
    fun `single character domains work correctly`() {
        val filter = newFilter()
        filter.add("a.io")
        assertTrue(filter.contains("a.io"))
        assertFalse(filter.contains("b.io"))
    }

    @Test
    fun `long domain names are handled correctly`() {
        val filter = newFilter()
        val long = "this-is-a-very-long-disposable-email-domain-name-that-exceeds-normal-length.com"
        filter.add(long)
        assertTrue(filter.contains(long))
    }

    @Test
    fun `non-ASCII input does not throw`() {
        val filter = newFilter()
        val idn = "münchen.de"
        assertDoesNotThrow { filter.add(idn) }
        assertTrue(filter.contains(idn))
    }
}
