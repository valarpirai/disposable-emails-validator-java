package org.disposableemail.dnsoverhttps

class CloudFlareDnsResolver : DnsResolver() {
    override fun getResolverUrl(): String = DnsConstants.CLOUD_FLARE_DNS_RESOLVER_URL
}
