package org.disposableemail.dnsoverhttps

class GoogleDnsResolver : DnsResolver() {
    override fun getResolverUrl(): String = DnsConstants.GOOGLE_DNS_RESOLVER_URL
}
