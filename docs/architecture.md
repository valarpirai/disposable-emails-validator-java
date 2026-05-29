# Architecture

## Overview

`disposable-emails-validator-java` is a Kotlin library that detects disposable email addresses using two complementary strategies: an **offline bloom filter** for fast local lookups, and **DNS-over-HTTPS** for live MX record verification.

## Package Structure

```
org.disposableemail
├── DisposableEmail.kt        # Public API singleton
├── Constants.kt              # Regex pattern and domain list URLs
├── bloomfilter/
│   ├── BloomFilter.kt        # Interface
│   ├── InMemoryBloomFilter.kt
│   └── BitArray.kt
└── dnsoverhttps/
    ├── DnsResolver.kt        # Abstract base (OkHttp + Moshi)
    ├── CloudFlareDnsResolver.kt
    ├── GoogleDnsResolver.kt
    ├── Resolver.kt           # Static facade + DnsResolverType enum
    ├── Constants.kt
    └── pojo/
        ├── DnsQuery.kt
        └── DnsResponse.kt
```

## Public API

`DisposableEmail` is a singleton (lazily created via companion object). All public methods are static-style calls on the companion.

| Method | Mode | Description |
|---|---|---|
| `isDisposable(email)` | Offline | Bloom filter lookup |
| `hasValidMailDomain(domain, resolver?)` | Online | MX record check via DoH |
| `getDomainDetails(email, resolver?)` | Both | Returns `DomainDetails(disposableDomain, mxRecordPresent)` |
| `refreshDisposableDomains()` | Online | Downloads latest list, rebuilds filter in memory, swaps atomically |
| `addDomainToWhitelist(domain)` | — | Overrides bloom filter result to `false` |
| `addDomainToBlacklist(domain)` | — | Overrides bloom filter result to `true` |

## Offline Detection: Bloom Filter

The bloom filter is seeded at startup from `src/main/resources/disposable-domains-encoded.txt`, a JSON-encoded `LongArray` that is the raw bit array of a pre-built filter.

```
disposable-domains-encoded.txt
        │  (JSON LongArray)
        ▼
   BitArray  ◄─── InMemoryBloomFilter
                        │
               MurmurHash3 (hashCount iterations)
                        │
                   contains(domain)?
```

Parameters: `expectedInsertionCount = 200,000`, `falsePositivePercentage = 0.01`.

Hash count and bit size are derived from these using standard bloom filter formulas:
- `bitSize = -(n * ln(p)) / ln(2)²`
- `hashCount = (bitSize / n) * ln(2)`

Each `add`/`contains` call hashes the value `hashCount` times using different MurmurHash3 seeds (`1..hashCount`).

**Whitelist/blacklist** are plain `MutableSet<String>` checked before the bloom filter in `isDisposable()`.

### Refresh Flow

`refreshDisposableDomains()` builds a *new* `InMemoryBloomFilter` from the live domain list URL, then swaps `bloomFilter` — the old filter is abandoned and GC'd (optionally triggered immediately).

## Online Detection: DNS-over-HTTPS

```
Resolver.isMxRecordPresent(domain, type)
        │
        ▼
DnsResolverType  ──CLOUD_FLARE──►  GoogleDnsResolver   (note: enum vs impl are swapped in Resolver.kt)
                 ──GOOGLE──────►  CloudFlareDnsResolver
        │
        ▼
DnsResolver.resolve(DnsQuery)
        │  OkHttp GET with Accept: application/dns-json
        ▼
DnsResponse.Answer != null  →  MX record present
```

> **Note:** There is a known inversion in `Resolver.getResolver()` — `CLOUD_FLARE` selects `GoogleDnsResolver` and `GOOGLE` selects `CloudFlareDnsResolver`.

DNS responses are deserialized with Moshi. `DnsResponse` mirrors the JSON-over-HTTPS DNS wire format (RFC 8484).

## Publishing

The library is packaged as a shadow (fat) JAR via the `shadow` Gradle plugin. Internal dependencies (`okhttp3`, `okio`, `moshi`, `commons-codec`) are relocated under `org.valarpirai.shaded.*` to avoid classpath conflicts in consumer projects.

Published to GitHub Packages at `maven.pkg.github.com/valarpirai/disposable-emails-validator-java`. CI triggers on GitHub release creation.
