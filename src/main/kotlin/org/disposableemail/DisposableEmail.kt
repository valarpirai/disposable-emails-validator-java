package org.disposableemail

import okhttp3.OkHttpClient
import okhttp3.Request
import org.disposableemail.Configurations.Companion.EMAIL_PATTERN
import org.disposableemail.Configurations.Companion.GENERIC_DOMAIN_LISTS_TXT
import org.disposableemail.bloomfilter.InMemoryBloomFilter
import org.disposableemail.dnsoverhttps.DNS_RESOLVER_TYPE
import org.disposableemail.dnsoverhttps.DnsResolver
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader

class DisposableEmail private constructor() {
    private val maxDomains = 200_000
    private val falsePositivePercentage = 0.01
    private var bloomFilter = InMemoryBloomFilter(
        maxDomains,
        falsePositivePercentage
    )

    private var whiteListedDomains = mutableSetOf("")
    private var blackListedDomains = mutableSetOf("")

    companion object {
        private var instance: DisposableEmail? = null

        private fun getInstance(): DisposableEmail {
            if (instance != null)
                return instance as DisposableEmail

            instance = DisposableEmail()
            instance!!.loadDomainsFromResourceFile()
            return instance as DisposableEmail
        }

        /**
         * Verify the given email address is a Disposable mail box. Also, check the given email actually exists
         *
         */
        fun isDisposableEmail(email: String, checkDns: Boolean = true, dnsResolver: DNS_RESOLVER_TYPE = DNS_RESOLVER_TYPE.CLOUD_FLARE): Boolean {
            val domain = getInstance().extractDomain(email)

            if (!getInstance().isDisposable(domain)) {
                return checkDns && !isValidMailDomain(domain, dnsResolver)
            }
            return true
        }

        private fun isValidMailDomain(domain: String, dnsResolver: DNS_RESOLVER_TYPE): Boolean {
            return DnsResolver.isMxRecordPresent(domain, dnsResolver)
        }

        fun addDomainToWhitelist(domain: String) {
            getInstance().whiteListedDomains.add(domain)
        }

        fun addDomainToBlacklist(domain: String) {
            getInstance().blackListedDomains.add(domain)
        }

        fun removeDomainToWhitelist(domain: String) {
            getInstance().whiteListedDomains.remove(domain)
        }

        fun removeDomainToBlacklist(domain: String) {
            getInstance().blackListedDomains.remove(domain)
        }

        /**
         * Download latest disposable email domain list from Github
         */
        fun refreshDisposableDomains(performGc: Boolean = true) {
            getInstance().refreshDisposableDomains(performGc)
        }
    }

    /**
     * By providing email address, it returns whether the domain is a disposable domain
     *
     * @param domain - domain of a Email Address
     */
    fun isDisposable(domain: String): Boolean {
        if (whiteListedDomains.contains(domain)) {
            return false
        } else if(blackListedDomains.contains(domain)) {
            return true
        }
        return bloomFilter.contains(domain)
    }

    private fun extractDomain(email: String): String {
        val lastIndex: Int = email.lastIndexOf('@')
        var domain: String = email.lowercase()
        if (lastIndex >= 0) {
            domain = email.substring(lastIndex + 1).lowercase()
        }
        return domain
    }

    /**
     * Checks whether the give email address is a Valid email address
     */
    fun validEmail(email: String): Boolean {
        if (!email.contains("@")) {
            return false
        }
        return !EMAIL_PATTERN.matcher(email).matches()
    }

    private fun addDomainToFilter(domain: String) {
        bloomFilter.add(domain)
    }

    /**
     * Load Disposable Email domains from ClassPath resource TXT file
     */
    @Throws(IOException::class)
    private fun loadDomainsFromResourceFile() {
        val classLoader = javaClass.classLoader
        val inputStream = classLoader.getResourceAsStream(Configurations.DOMAIN_RESOURCE_FILE_NAME)
        inputStream?.use {
            BufferedReader(InputStreamReader(it)).use { br
                ->
                var line: String?
                while ((br.readLine().also { line = it }) != null) {
                    line?.let { addDomainToFilter(it) }
                }
            }
        }
    }

    /**
     *  Download latest disposable emails from URL, then build BloomFilter and use it.
     */
    fun refreshDisposableDomains(performGc: Boolean) {
        val tempBloomFilter = InMemoryBloomFilter(
            maxDomains,
            falsePositivePercentage
        )

        // Download latest domain list
        val client = OkHttpClient()
        val request = Request.Builder().url(GENERIC_DOMAIN_LISTS_TXT)
            .build()
        val response = client.newCall(request).execute()

        val `in`: InputStream? = response.body?.byteStream()
        if (`in` != null) {
            val br = BufferedReader(InputStreamReader(`in`))
            var line: String?
            while ((br.readLine().also { line = it }) != null) {
                line?.let { tempBloomFilter.add(it) }
            }

            // Swap
            bloomFilter = tempBloomFilter
        }
        response.body?.close()
        if (performGc)
            System.gc()
    }
}
