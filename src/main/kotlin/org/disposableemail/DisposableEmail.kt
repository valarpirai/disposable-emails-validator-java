package org.disposableemail

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.Request
import org.disposableemail.Constants.GENERIC_DOMAIN_LISTS_TXT
import org.disposableemail.Constants.DOMAIN_RESOURCE_FILE_NAME
import org.disposableemail.Constants.EMAIL_PATTERN
import org.disposableemail.bloomfilter.InMemoryBloomFilter
import org.disposableemail.dnsoverhttps.DnsResolverType
import org.disposableemail.dnsoverhttps.Resolver
import java.io.BufferedReader
import java.io.InputStreamReader

class DisposableEmail private constructor() {
    private val maxDomains = 200_000
    private val falsePositivePercentage = 0.01

    @Volatile private var bloomFilter = InMemoryBloomFilter(
        loadDomainDataFromResourceFile(),
        maxDomains,
        falsePositivePercentage
    )

    private val whiteListedDomains = mutableSetOf<String>()
    private val blackListedDomains = mutableSetOf<String>()

    companion object {
        @Volatile private var instance: DisposableEmail? = null
        private val httpClient = OkHttpClient()

        private fun getInstance(): DisposableEmail =
            instance ?: synchronized(this) {
                instance ?: DisposableEmail().also { instance = it }
            }

        fun getDomainDetails(email: String, dnsResolver: DnsResolverType = DnsResolverType.CLOUD_FLARE): DomainDetails {
            val domain = getInstance().extractDomain(email)
            return DomainDetails(getInstance().isDisposable(domain), hasValidMailDomain(domain, dnsResolver))
        }

        fun isDisposable(email: String): Boolean {
            val domain = getInstance().extractDomain(email)
            return getInstance().isDisposable(domain)
        }

        fun hasValidMailDomain(domain: String, dnsResolver: DnsResolverType = DnsResolverType.CLOUD_FLARE): Boolean =
            Resolver.isMxRecordPresent(domain, dnsResolver)

        fun validEmail(email: String): Boolean = getInstance().validEmail(email)

        fun addDomainToWhitelist(domain: String) { getInstance().whiteListedDomains.add(domain) }
        fun removeDomainFromWhitelist(domain: String) { getInstance().whiteListedDomains.remove(domain) }

        fun addDomainToBlacklist(domain: String) { getInstance().blackListedDomains.add(domain) }
        fun removeDomainFromBlacklist(domain: String) { getInstance().blackListedDomains.remove(domain) }

        fun refreshDisposableDomains(performGc: Boolean = true) {
            getInstance().refreshDisposableDomains(performGc)
        }
    }

    fun isDisposable(domain: String): Boolean {
        if (whiteListedDomains.contains(domain)) return false
        if (blackListedDomains.contains(domain)) return true
        return bloomFilter.contains(domain)
    }

    fun validEmail(email: String): Boolean {
        if (!email.contains("@")) return false
        return EMAIL_PATTERN.matcher(email).matches()
    }

    private fun extractDomain(email: String): String {
        val lastIndex = email.lastIndexOf('@')
        return if (lastIndex >= 0) email.substring(lastIndex + 1).lowercase() else email.lowercase()
    }

    private fun loadDomainDataFromResourceFile(): LongArray? {
        val moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()
        val jsonAdapter: JsonAdapter<LongArray> = moshi.adapter(LongArray::class.java)
        val inputStream = javaClass.classLoader.getResourceAsStream(DOMAIN_RESOURCE_FILE_NAME) ?: return null
        return inputStream.bufferedReader().use { jsonAdapter.fromJson(it.readText()) }
    }

    private fun refreshDisposableDomains(performGc: Boolean) {
        val tempBloomFilter = InMemoryBloomFilter(null, maxDomains, falsePositivePercentage)
        val request = Request.Builder().url(GENERIC_DOMAIN_LISTS_TXT).build()

        httpClient.newCall(request).execute().use { response ->
            response.body?.byteStream()?.let { stream ->
                BufferedReader(InputStreamReader(stream)).use { br ->
                    br.lineSequence().forEach { tempBloomFilter.add(it) }
                }
                bloomFilter = tempBloomFilter
            }
        }
        if (performGc) System.gc()
    }
}
