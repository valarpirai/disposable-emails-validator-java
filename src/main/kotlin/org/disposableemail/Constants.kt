package org.disposableemail

import java.util.regex.Pattern

object Constants {
    val EMAIL_PATTERN: Pattern = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
    const val DOMAIN_RESOURCE_FILE_NAME = "disposable-domains-encoded.txt"
    const val GENERIC_DOMAIN_LISTS_TXT = "https://disposable.github.io/disposable-email-domains/domains.txt"
}
