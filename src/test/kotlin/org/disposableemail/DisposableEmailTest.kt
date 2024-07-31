package org.disposableemail

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class DisposableEmailTest {

    @Test
    fun test_DisposableEmail() {
        Assertions.assertTrue(DisposableEmail.isDisposableEmail("yopmail.com"))
        Assertions.assertTrue(DisposableEmail.isDisposableEmail("mailsac.com"))
        Assertions.assertTrue(DisposableEmail.isDisposableEmail("mailsac.co"))
        Assertions.assertFalse(DisposableEmail.isDisposableEmail("gmail.co"))
    }

    @Test
    fun test_disposable_domain() {
        Assertions.assertTrue(DisposableEmail.isDisposableEmail("yopmail.com"))
    }

    @Test
    fun test_invalid_domain() {
        Assertions.assertTrue(DisposableEmail.isDisposableEmail("invaliddomain12.com"))
    }

    @Test
    fun test_invalid_domain_dont_check_dns() {
        Assertions.assertFalse(DisposableEmail.isDisposableEmail("invaliddomain12.com", false))
    }

    @Test
    fun test_valid_domain() {
        Assertions.assertFalse(DisposableEmail.isDisposableEmail("gmail.com"))
    }

    @Test
    fun test_valid_domain_with_no_dns_verification() {
        Assertions.assertFalse(DisposableEmail.isDisposableEmail("gmail.com", false))
    }

    @Test
    fun test_RefreshData() {
        Assertions.assertTrue(DisposableEmail.isDisposableEmail("yopmail.com"))
        DisposableEmail.refreshDisposableDomains()
        Assertions.assertTrue(DisposableEmail.isDisposableEmail("yopmail.com"))
    }
}
