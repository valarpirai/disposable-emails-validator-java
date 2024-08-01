package org.disposableemail;

import java.util.regex.Pattern

class Configurations {

  companion object {
    val EMAIL_PATTERN: Pattern = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")

//    const val DOMAIN_RESOURCE_FILE_NAME = "disposable-email-domains.txt"
    const val DOMAIN_RESOURCE_FILE_NAME = "disposable-domains-encoded.txt"
    const val GENERIC_DOMAIN_LISTS_TXT = "https://disposable.github.io/disposable-email-domains/domains.txt"
    const val MX_VALIDATED_DOMAINS_LIST = "https://disposable.github.io/disposable-email-domains/domains_mx.txt"

    const val CLOUD_FLARE_DNS_RESOLVER_URL = "https://1.1.1.1/dns-query"
    const val GOOGLE_DNS_RESOLVER_URL = "https://dns.google/resolve"
  }
}
