package org.disposableemail

import org.disposableemail.dnsoverhttps.DNS_RESOLVER_TYPE
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

    @Test
    fun test_add_remove_blacklist_domain() {
        Assertions.assertFalse(DisposableEmail.isDisposable("gmail.com"))
        DisposableEmail.addDomainToBlacklist("gmail.com")
        Assertions.assertTrue(DisposableEmail.isDisposable("gmail.com"))
        DisposableEmail.removeDomainFromBlacklist("gmail.com")
        Assertions.assertFalse(DisposableEmail.isDisposable("gmail.com"))
    }

    @Test
    fun test_add_remove_whitelist_domain() {
        Assertions.assertTrue(DisposableEmail.isDisposable("mailsac.com"))
        DisposableEmail.addDomainToWhitelist("mailsac.com")
        Assertions.assertFalse(DisposableEmail.isDisposable("mailsac.com"))
        DisposableEmail.removeDomainFromWhitelist("mailsac.com")
        Assertions.assertTrue(DisposableEmail.isDisposable("mailsac.com"))
    }

    @Test
    fun test_get_domain_details() {
        var data = DisposableEmail.getDomainDetails("mailsac.com")
        Assertions.assertEquals(data["DISPOSABLE_DOMAIN"], true)
        Assertions.assertEquals(data["DNS_MX_PRESENT"], true)

        data = DisposableEmail.getDomainDetails("gmail.com")
        Assertions.assertEquals(data["DISPOSABLE_DOMAIN"], false)
        Assertions.assertEquals(data["DNS_MX_PRESENT"], true)

        data = DisposableEmail.getDomainDetails("nonexisting123.com")
        Assertions.assertEquals(data["DISPOSABLE_DOMAIN"], false)
        Assertions.assertEquals(data["DNS_MX_PRESENT"], false)
    }

    @Test
    fun test_check_dns_entry() {
        Assertions.assertTrue(DisposableEmail.hasValidMailDomain("gmail.com"))
        Assertions.assertTrue(DisposableEmail.hasValidMailDomain("gmail.com", DNS_RESOLVER_TYPE.GOOGLE))
        Assertions.assertTrue(DisposableEmail.hasValidMailDomain("mailsac.com"))
        Assertions.assertFalse(DisposableEmail.hasValidMailDomain("nonexisting123.com"))
    }

    @Test
    fun test_RefreshData() {
        Assertions.assertTrue(DisposableEmail.isDisposable("yopmail.com"))
        Assertions.assertTrue(DisposableEmail.isDisposable("mailsac.com"))
        Assertions.assertFalse(DisposableEmail.isDisposable("gmail.com"))

        DisposableEmail.refreshDisposableDomains() // Downloads latest data

        Assertions.assertTrue(DisposableEmail.isDisposable("yopmail.com"))
        Assertions.assertTrue(DisposableEmail.isDisposable("mailsac.com"))
        Assertions.assertFalse(DisposableEmail.isDisposable("gmail.com"))
    }
}
