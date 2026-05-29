package org.disposableemail.dnsoverhttps

import org.disposableemail.dnsoverhttps.pojo.DnsQuery
import org.disposableemail.dnsoverhttps.pojo.RecordType

enum class DnsResolverType { CLOUD_FLARE, GOOGLE }

object Resolver {
    fun isMxRecordPresent(domain: String, dnsResolver: DnsResolverType): Boolean {
        val query = DnsQuery(domain, RecordType.MX)
        val resp = getResolver(dnsResolver).resolve(query) ?: return false
        return !resp.Answer.isNullOrEmpty()
    }

    private fun getResolver(dnsResolver: DnsResolverType): DnsResolver =
        if (dnsResolver == DnsResolverType.CLOUD_FLARE) CloudFlareDnsResolver() else GoogleDnsResolver()
}
