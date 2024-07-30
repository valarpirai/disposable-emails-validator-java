package org.disposableemail.dnsoverhttps

import java.io.Serializable

class DnsResponse: Serializable {
    val Status: Int = 0
    val TC: Boolean = false
    val RD: Boolean = true
    val RA: Boolean = true
    val AD: Boolean = false
    val CD: Boolean = false

    val Question: List<Any>? = null
    val Answer: List<Any>? = null
}
