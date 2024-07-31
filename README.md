# Disposable email domain validator


## Usage
Check whether a given email address is disposable address
```
DisposableEmail.isDisposableEmail(hello@gmail.com) -> false
DisposableEmail.isDisposableEmail(hello@mailsac.com) -> true
```

To stop checking DNS
```
DisposableEmail.isDisposableEmail(hello@gmail.com, false) -> false
```

Use different DNS resolver
```
DisposableEmail.isDisposableEmail(hello@gmail.com, true, DNS_RESOLVER_TYPE.CLOUD_FLARE) -> false
DisposableEmail.isDisposableEmail(hello@gmail.com, true, DNS_RESOLVER_TYPE.GOOGLE) -> false
```

### Is this a disposable email? How it works?
- By default, we are checking the given email against the local copy of disposable email domains. If it is `false`, the checking DNS
  - We check DNS MX (mail) record for the domain. If MX record present, then return `true`
- Otherwise, returning `true` (disposable email domain)

### Getting latest domain list
This lib has a list of disposable email domains inside resources.
The following method will download latest disposable email list from `https://disposable.github.io/disposable-email-domains/domains.txt` and use it
```
DisposableEmail.refreshDisposableDomains()
```

### Tech details
- We are using BloomFilter a space-efficient probabilistic data structure
- We are using DNS over HTTPS to verify the MX Records
  - We have support CloudFlare and Google DNS resolver support
  
