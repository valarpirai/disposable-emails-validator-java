package com.disposableemail

import com.disposableemail.Configurations.Companion.EMAIL_PATTERN
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import java.util.regex.Matcher
import kotlin.reflect.KFunction1


class DisposableEmail private constructor() {
    private var trieData = Trie()
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
        return trieData.search(domain)
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
        trieData.insert(domain.lowercase())
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
//        Download latest domain list
//        Create new Trie object
//        Swap new trie with old Trie
    }

    fun fetchDomainsWithTxt(urlPath: String?): Set<String> {
        val disposableEmailDomainsSet: MutableSet<String> = HashSet()
        try {
            val url = URL(urlPath)
            val con: HttpURLConnection = url.openConnection() as HttpURLConnection
            con.setRequestMethod("GET")
            val `in` = BufferedReader(InputStreamReader(con.getInputStream()))
            var inputLine: String
            while ((`in`.readLine().also { inputLine = it }) != null) {
                disposableEmailDomainsSet.add(inputLine)
            }
            `in`.close()
            return disposableEmailDomainsSet
        } catch (ioe: IOException) {
            return disposableEmailDomainsSet
        }
    }
}

class Trie {
    private var root: Node
    var size = 0

    init {
        root = Node()
    }

    fun insert(word: String) {
        root.insert(word, 0)
        size++
    }

    fun search(word: String): Boolean {
        return root.search(word, 0)
    }

    fun startsWith(prefix: String): Boolean {
        return root.startsWith(prefix, 0)
    }

    internal inner class Node {
        private var nodes: Array<Node?> = arrayOfNulls(38)
        private var isEnd: Boolean = false

        /**
         * Supports [a-z0-9-.]
         *
         * return index for our Array
         */
        private fun getCode(char: Char): Int {
            val code = char.code
            if (code in 97..122)
                // 0-25
                return code - 97
            else if (code in 48..57)
                // 26-35
                return code - 22
            else if (code == 45 || code == 46)
                // 36 || 37
                return code - 9
            else {
                throw UnsupportedEncodingException("Unsupported value ($char) present in domain name")
            }
        }

        internal fun insert(word: String, idx: Int) {
            if (idx >= word.length) return
            val i = getCode(word[idx])
            if (nodes[i] == null) {
                nodes[i] = Node()
            }

            if (idx == word.length - 1) nodes[i]!!.isEnd = true
            nodes[i]!!.insert(word, idx + 1)
        }

        fun search(word: String, idx: Int): Boolean {
            if (idx >= word.length) return false
            val node = nodes[getCode(word[idx])] ?: return false
            if (idx == word.length - 1 && node.isEnd) return true

            return node.search(word, idx + 1)
        }

        fun startsWith(prefix: String, idx: Int): Boolean {
            if (idx >= prefix.length) return false
            val node = nodes[getCode(prefix[idx])] ?: return false
            if (idx == prefix.length - 1) return true

            return node.startsWith(prefix, idx + 1)
        }
    }
}
