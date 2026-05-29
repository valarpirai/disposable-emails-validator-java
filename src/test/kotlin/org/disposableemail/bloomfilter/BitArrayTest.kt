package org.disposableemail.bloomfilter

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class BitArrayTest {

    @Test
    fun `set and get round-trip for arbitrary indices`() {
        val ba = BitArray(128)
        assertFalse(ba.get(0))
        assertFalse(ba.get(63))
        assertFalse(ba.get(64))
        assertFalse(ba.get(127))

        ba.set(0)
        ba.set(63)
        ba.set(64)
        ba.set(127)

        assertTrue(ba.get(0))
        assertTrue(ba.get(63))
        assertTrue(ba.get(64))
        assertTrue(ba.get(127))
    }

    @Test
    fun `unset bits are not affected by neighbouring sets`() {
        val ba = BitArray(128)
        ba.set(0)
        ba.set(2)

        assertFalse(ba.get(1))
        assertFalse(ba.get(3))
    }

    @Test
    fun `set returns true only when bit was previously unset`() {
        val ba = BitArray(64)
        assertTrue(ba.set(10))
        assertFalse(ba.set(10))
    }

    @Test
    fun `bitCount tracks number of set bits`() {
        val ba = BitArray(128)
        assertEquals(0, ba.bitCount())

        ba.set(5)
        assertEquals(1, ba.bitCount())

        ba.set(70)
        assertEquals(2, ba.bitCount())

        ba.set(5) // already set — no change
        assertEquals(2, ba.bitCount())
    }

    @Test
    fun `bitSize returns total allocated bits`() {
        assertEquals(64, BitArray(64).bitSize())
        assertEquals(128, BitArray(128).bitSize())
    }

    @Test
    fun `copy is independent of original`() {
        val original = BitArray(64)
        original.set(10)

        val copy = original.copy()
        copy.set(20)

        assertTrue(original.get(10))
        assertFalse(original.get(20))
        assertTrue(copy.get(10))
        assertTrue(copy.get(20))
    }

    @Test
    fun `constructor from LongArray restores set bits`() {
        val ba = BitArray(128)
        ba.set(7)
        ba.set(77)

        val restored = BitArray(ba.data)
        assertTrue(restored.get(7))
        assertTrue(restored.get(77))
        assertFalse(restored.get(0))
        assertEquals(2, restored.bitCount())
    }

    @Test
    fun `equals and hashCode match for identical data`() {
        val a = BitArray(64)
        a.set(1)
        val b = BitArray(64)
        b.set(1)

        assertEquals(a, b)
        assertEquals(a.hashCode(), b.hashCode())
    }

    @Test
    fun `equals returns false for different data`() {
        val a = BitArray(64)
        a.set(1)
        val b = BitArray(64)
        b.set(2)

        assertNotEquals(a, b)
    }
}
