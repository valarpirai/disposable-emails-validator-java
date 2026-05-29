package org.disposableemail

import org.disposableemail.dnsoverhttps.DnsResolverType
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class DisposableEmailTest {

    @AfterEach
    fun cleanupSingletonState() {
        DisposableEmail.removeDomainFromWhitelist("mailsac.com")
        DisposableEmail.removeDomainFromBlacklist("gmail.com")
    }

    @Test
    fun `isDisposable returns true for known disposable domains`() {
        assertTrue(DisposableEmail.isDisposable("yopmail.com"))
        assertTrue(DisposableEmail.isDisposable("mailsac.com"))
    }

    @Test
    fun `isDisposable returns false for legitimate domains`() {
        assertFalse(DisposableEmail.isDisposable("gmail.com"))
        assertFalse(DisposableEmail.isDisposable("mailsac.co"))
        assertFalse(DisposableEmail.isDisposable("gmail.co"))
    }

    @Test
    fun `isDisposable accepts email address and extracts domain`() {
        assertFalse(DisposableEmail.isDisposable("hello@gmail.com"))
        assertTrue(DisposableEmail.isDisposable("hello@mailsac.com"))
    }

    @Test
    fun `isDisposable is case-insensitive`() {
        assertTrue(DisposableEmail.isDisposable("hello@MAILSAC.COM"))
        assertFalse(DisposableEmail.isDisposable("hello@GMAIL.COM"))
    }

    @Test
    fun `isDisposable returns false for unknown domains`() {
        assertFalse(DisposableEmail.isDisposable("invaliddomain12.com"))
    }

    @Test
    fun `whitelist overrides disposable detection`() {
        assertTrue(DisposableEmail.isDisposable("mailsac.com"))

        DisposableEmail.addDomainToWhitelist("mailsac.com")
        assertFalse(DisposableEmail.isDisposable("mailsac.com"))
        assertFalse(DisposableEmail.isDisposable("hello@mailsac.com"))

        DisposableEmail.removeDomainFromWhitelist("mailsac.com")
        assertTrue(DisposableEmail.isDisposable("mailsac.com"))
    }

    @Test
    fun `blacklist overrides legitimate domain detection`() {
        assertFalse(DisposableEmail.isDisposable("gmail.com"))

        DisposableEmail.addDomainToBlacklist("gmail.com")
        assertTrue(DisposableEmail.isDisposable("gmail.com"))
        assertTrue(DisposableEmail.isDisposable("hello@gmail.com"))

        DisposableEmail.removeDomainFromBlacklist("gmail.com")
        assertFalse(DisposableEmail.isDisposable("gmail.com"))
    }

    @Test
    fun `getDomainDetails returns correct disposable and MX flags`() {
        var details = DisposableEmail.getDomainDetails("mailsac.com")
        assertTrue(details.disposableDomain)
        assertTrue(details.mxRecordPresent)

        details = DisposableEmail.getDomainDetails("gmail.com")
        assertFalse(details.disposableDomain)
        assertTrue(details.mxRecordPresent)

        details = DisposableEmail.getDomainDetails("nonexisting123.com")
        assertFalse(details.disposableDomain)
        assertFalse(details.mxRecordPresent)
    }

    @Test
    fun `hasValidMailDomain checks MX record via CloudFlare and Google`() {
        assertTrue(DisposableEmail.hasValidMailDomain("gmail.com"))
        assertTrue(DisposableEmail.hasValidMailDomain("gmail.com", DnsResolverType.GOOGLE))
        assertTrue(DisposableEmail.hasValidMailDomain("mailsac.com"))
        assertFalse(DisposableEmail.hasValidMailDomain("nonexisting123.com"))
    }

    @Test
    fun `validEmail accepts well-formed addresses`() {
        assertTrue(DisposableEmail.validEmail("user@example.com"))
        assertTrue(DisposableEmail.validEmail("hello@gmail.com"))
        assertTrue(DisposableEmail.validEmail("first.last+tag@sub.domain.org"))
    }

    @Test
    fun `validEmail rejects malformed addresses`() {
        assertFalse(DisposableEmail.validEmail("notanemail"))
        assertFalse(DisposableEmail.validEmail("missing@tld"))
        assertFalse(DisposableEmail.validEmail("@nodomain.com"))
        assertFalse(DisposableEmail.validEmail(""))
        assertFalse(DisposableEmail.validEmail("noatsign.com"))
    }

    @Test
    fun `refreshDisposableDomains preserves known disposable and legitimate domains`() {
        DisposableEmail.refreshDisposableDomains()

        assertTrue(DisposableEmail.isDisposable("yopmail.com"))
        assertTrue(DisposableEmail.isDisposable("mailsac.com"))
        assertFalse(DisposableEmail.isDisposable("gmail.com"))
    }
}
