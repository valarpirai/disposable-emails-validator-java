package org.disposableemail.dnsoverhttps

class GoogleDnsResolver: DnsResolver() {
    override fun getResolverUrl(): String {
        return Constants.GOOGLE_DNS_RESOLVER_URL
    }
}
