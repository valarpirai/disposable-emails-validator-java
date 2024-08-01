package org.disposableemail

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class DisposableEmailTest {

    @Test
    fun test_DisposableEmail() {
        Assertions.assertTrue(DisposableEmail.isDisposableEmailDomain("yopmail.com"))
        Assertions.assertTrue(DisposableEmail.isDisposableEmailDomain("mailsac.com"))
        Assertions.assertFalse(DisposableEmail.isDisposableEmailDomain("mailsac.co"))
        Assertions.assertFalse(DisposableEmail.isDisposableEmailDomain("gmail.co"))
    }

    @Test
    fun test_disposable_domain() {
        Assertions.assertTrue(DisposableEmail.isDisposableEmailDomain("yopmail.com"))
    }

    @Test
    fun test_invalid_domain() {
        Assertions.assertFalse(DisposableEmail.isDisposableEmailDomain("invaliddomain12.com"))
    }

    @Test
    fun test_valid_domain() {
        Assertions.assertFalse(DisposableEmail.isDisposableEmailDomain("gmail.com"))
    }

//    Whitelist
//    Blacklist
//    Validate DNS case
//  getEmailDomainDetails

    @Test
    fun test_valid_domain_with_no_dns_verification() {
        Assertions.assertFalse(DisposableEmail.isDisposableEmailDomain("gmail.com"))
    }

    @Test
    fun test_RefreshData() {
        Assertions.assertTrue(DisposableEmail.isDisposableEmailDomain("yopmail.com"))
        DisposableEmail.refreshDisposableDomains()
        Assertions.assertTrue(DisposableEmail.isDisposableEmailDomain("yopmail.com"))
    }
}
