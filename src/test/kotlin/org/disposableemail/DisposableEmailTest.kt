package org.disposableemail

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class DisposableEmailTest {

    @Test
    fun testDisposableEmail() {
        Assertions.assertTrue(DisposableEmail.isDisposableEmail("yopmail.com"))
        Assertions.assertTrue(DisposableEmail.isDisposableEmail("mailsac.com"))
        Assertions.assertFalse(DisposableEmail.isDisposableEmail("mailsac.co"))
        Assertions.assertFalse(DisposableEmail.isDisposableEmail("gmail.co"))
    }

    @Test
    fun testRefreshData() {
        Assertions.assertFalse(DisposableEmail.isDisposableEmail("zzz.com"))
        DisposableEmail.refreshDisposableDomains()
        Assertions.assertTrue(DisposableEmail.isDisposableEmail("yopmail.com"))
        Assertions.assertTrue(DisposableEmail.isDisposableEmail("mailsac.com"))
        Assertions.assertFalse(DisposableEmail.isDisposableEmail("mailsac.co"))
    }
}
