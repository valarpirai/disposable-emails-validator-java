package org.disposableemail

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class DisposableEmailTest {

    @Test
    fun testDisposableEmail() {
        Assertions.assertTrue(DisposableEmail.isDisposable("yopmail.com"))
        Assertions.assertTrue(DisposableEmail.isDisposable("mailsac.com"))
        Assertions.assertFalse(DisposableEmail.isDisposable("mailsac.co"))
        Assertions.assertFalse(DisposableEmail.isDisposable("gmail.co"))
    }

    @Test
    fun testRefreshData() {
        Assertions.assertFalse(DisposableEmail.isDisposable("zzz.com"))
//        DisposableEmail.refreshDisposableDomains()
        Assertions.assertTrue(DisposableEmail.isDisposable("yopmail.com"))
        Assertions.assertTrue(DisposableEmail.isDisposable("mailsac.com"))
        Assertions.assertFalse(DisposableEmail.isDisposable("mailsac.co"))
    }
}
