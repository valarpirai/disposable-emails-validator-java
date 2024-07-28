package com.example

import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.io.UnsupportedEncodingException
import kotlin.reflect.KFunction1


class DisposableEmail private constructor() {
    private var trieData: Trie? = null
    private var whiteListedDomains: Set<String>? = null
    private var blackListedDomains: Set<String>? = null

    companion object {
        private var instance: DisposableEmail? = null

        fun getInstance(): DisposableEmail {
            if (instance != null)
                return instance as DisposableEmail

            instance = DisposableEmail()
            instance!!.initialize()
            return instance as DisposableEmail
        }
    }

    private fun initialize() {
        trieData = Trie()
        println("Size -> " + trieData!!.size)
        val classLoader = javaClass.classLoader
        val inputStream = classLoader.getResourceAsStream("disposable-email-domains.txt")
        inputStream?.let { readFromInputStream(it, ::build) }
        println("Size -> " + trieData!!.size)
    }

    private fun build(domain: String) {
        trieData!!.insert(domain)
    }

    fun isDisposable(email: String): Boolean {
//        Configurations.GENERIC_DOMAIN_LISTS_TXT
        return trieData!!.search(email)
    }

    @Throws(IOException::class)
    private fun readFromInputStream(inputStream: InputStream, function: KFunction1<String, Unit>) {
        val resultStringBuilder = StringBuilder()
        BufferedReader(InputStreamReader(inputStream)).use { br
            ->
            var line: String?
            while ((br.readLine().also { line = it }) != null) {
                line?.let { function.invoke(it) }
//                resultStringBuilder.append(line).append("\n")
            }
        }
//        return resultStringBuilder.toString()
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
                println("$char $code")
                throw UnsupportedEncodingException("Unsupported value present in domain name")
            }
        }

        internal fun insert(domain: String, idx: Int) {
            val word = domain.lowercase()
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