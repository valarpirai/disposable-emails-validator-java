package org.disposableemail

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class DisposableEmailTest {

    @Test
    fun test_DisposableEmail() {
        Assertions.assertTrue(DisposableEmail.isDisposable("yopmail.com"))
        Assertions.assertTrue(DisposableEmail.isDisposable("mailsac.com"))
        Assertions.assertFalse(DisposableEmail.isDisposable("mailsac.co"))
        Assertions.assertFalse(DisposableEmail.isDisposable("gmail.co"))
    }

    @Test
    fun test_disposable_domain() {
        Assertions.assertTrue(DisposableEmail.isDisposable("yopmail.com"))
    }

    @Test
    fun test_invalid_domain() {
        Assertions.assertFalse(DisposableEmail.isDisposable("invaliddomain12.com"))
    }

    @Test
    fun test_valid_domain() {
        Assertions.assertFalse(DisposableEmail.isDisposable("gmail.com"))
    }

//    Whitelist
//    Blacklist
//    Validate DNS case
//  getDomainDetails

    @Test
    fun test_valid_domain_with_no_dns_verification() {
        Assertions.assertFalse(DisposableEmail.isDisposable("gmail.com"))
    }

    @Test
    fun test_RefreshData() {
        Assertions.assertTrue(DisposableEmail.isDisposable("yopmail.com"))
        DisposableEmail.refreshDisposableDomains()
        Assertions.assertTrue(DisposableEmail.isDisposable("yopmail.com"))
    }
}
