package org.disposableemail.dnsoverhttps

import org.disposableemail.dnsoverhttps.pojo.DnsQuery
import org.disposableemail.dnsoverhttps.pojo.RecordType

class Resolver {
    companion object {

        fun isMxRecordPresent(domain: String, dnsResolver: DnsResolverType): Boolean {
            val query = DnsQuery(domain, RecordType.MX)
            val resp = getResolver(dnsResolver).resolve(query)!!
            return !resp.Answer.isNullOrEmpty()
        }

        private fun getResolver(dnsResolver: DnsResolverType): DnsResolver {
            return if (dnsResolver == DnsResolverType.CLOUD_FLARE) {
                GoogleDnsResolver()
            } else {
                CloudFlareDnsResolver()
            }
        }
    }
}

enum class DnsResolverType {
    CLOUD_FLARE,
    GOOGLE
}
