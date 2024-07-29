package org.disposableemail

import org.disposableemail.Configurations.Companion.EMAIL_PATTERN
import org.disposableemail.Configurations.Companion.GENERIC_DOMAIN_LISTS_TXT
import org.disposableemail.bloomfilter.InMemoryBloomFilter
import java.io.*
import java.net.URL
import java.nio.channels.Channels
import java.nio.channels.ReadableByteChannel
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.reflect.KFunction1


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
            instance!!.initialize()
            return instance as DisposableEmail
        }

        fun isDisposable(email: String): Boolean {
            return getInstance().isDisposable(email)
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

        fun refreshDisposableDomains() {
            getInstance().refreshDisposableDomains()
        }
    }

    private fun initialize() {
        readDomainsFromResourceFile()
    }

    /**
     * By providing email address, it returns whether the domain is a disposable domain
     *
     * @param email - Email Address
     */
    fun isDisposable(email: String): Boolean {
        val lastIndex: Int = email.lastIndexOf('@')
        var domain: String = email.lowercase()
        if (lastIndex >= 0) {
            domain = email.substring(lastIndex + 1).lowercase()
        }

        if (whiteListedDomains.contains(domain)) {
            return false
        } else if(blackListedDomains.contains(domain)) {
            return true
        }
        return bloomFilter.contains(domain)
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

    private fun buildDomainDataSet(domain: String) {
        bloomFilter.add(domain)
    }

    private fun readDomainsFromResourceFile() {
        val classLoader = javaClass.classLoader
        val inputStream = classLoader.getResourceAsStream(Configurations.DOMAIN_RESOURCE_FILE_NAME)
        inputStream?.let { readFromInputStream(it, ::buildDomainDataSet) }
    }

    @Throws(IOException::class)
    private fun readFromInputStream(inputStream: InputStream, function: KFunction1<String, Unit>) {
        BufferedReader(InputStreamReader(inputStream)).use { br
            ->
            var line: String?
            while ((br.readLine().also { line = it }) != null) {
                line?.let { function.invoke(it) }
            }
        }
    }

    fun refreshDisposableDomains() {
        // Download latest domain list
        val tmpFile = "/tmp/generic-domains.txt"
        downloadUsingNIO(GENERIC_DOMAIN_LISTS_TXT, tmpFile)

        val filePath = Paths.get(tmpFile)
        val charset: Charset = StandardCharsets.UTF_8
        var error = false
        val tempBloomFilter = InMemoryBloomFilter(
            maxDomains,
            falsePositivePercentage
        )
        // Build
        try {
            Files.newBufferedReader(filePath, charset).use { bufferedReader ->
                var line: String?
                while ((bufferedReader.readLine().also { line = it }) != null) {
                    line?.let { tempBloomFilter.add(it) }
                }
            }
        } catch (_: IOException) {
            error = true
        }

        // Swap
        if (!error) {
            bloomFilter = tempBloomFilter
            println("Data refresh completed..")
        } else {
            println("Data refresh failed..")
        }
        System.gc()
    }

    @Throws(IOException::class)
    private fun downloadUsingNIO(urlStr: String, file: String) {
        println("Downloading latest file..")
        val url = URL(urlStr)
        val rbc: ReadableByteChannel = Channels.newChannel(url.openStream())
        val fos = FileOutputStream(file)
        fos.channel.transferFrom(rbc, 0, Long.MAX_VALUE)
        fos.close()
        rbc.close()
    }
}
