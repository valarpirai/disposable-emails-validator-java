package org.disposableemail.dnsoverhttps

class CloudFlareDnsResolver: DnsResolver() {
    override fun getResolverUrl(): String {
        return Constants.CLOUD_FLARE_DNS_RESOLVER_URL
    }
}
