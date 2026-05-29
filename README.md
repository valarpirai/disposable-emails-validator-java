# Disposable Email Validator

Detect disposable email addresses in Java/Kotlin. Works offline with a bloom filter. Checks MX records online via DNS-over-HTTPS.

## Install

Available on [GitHub Packages](https://github.com/valarpirai/disposable-emails/packages/2217546).

```xml
<repository>
  <id>github</id>
  <url>https://<github_user>:<github_personal_access_token>@maven.pkg.github.com/valarpirai/disposable-emails-validator-java</url>
</repository>
```

```xml
<dependency>
  <groupId>org.valarpirai</groupId>
  <artifactId>disposable-email</artifactId>
  <version>{version}</version>
</dependency>
```

## Usage

### Refresh the domain list

The library ships with a bundled domain list. Call this once at startup to fetch the latest one.

```kotlin
DisposableEmail.refreshDisposableDomains()
```

### Check a domain

```kotlin
DisposableEmail.isDisposable("hello@gmail.com")   // false
DisposableEmail.isDisposable("hello@mailsac.com") // true
```

### Check MX records

```kotlin
DisposableEmail.hasValidMailDomain("hello@mailsac.com") // true  — disposable but has MX
DisposableEmail.hasValidMailDomain("god.com")           // false — no MX record
```

### Get full domain details

```kotlin
val details = DisposableEmail.getDomainDetails("mailsac.com")
details.disposableDomain  // true
details.mxRecordPresent   // true
```

### Choose a DNS resolver

```kotlin
DisposableEmail.hasValidMailDomain("hello@gmail.com", DnsResolverType.CLOUD_FLARE)
DisposableEmail.hasValidMailDomain("hello@gmail.com", DnsResolverType.GOOGLE)
```

### Whitelist a domain

```kotlin
DisposableEmail.addDomainToWhitelist("mailsac.com")
DisposableEmail.isDisposable("hello@mailsac.com") // false

DisposableEmail.removeDomainFromWhitelist("mailsac.com")
DisposableEmail.isDisposable("mailsac.com")       // true
```

### Blacklist a domain

```kotlin
DisposableEmail.addDomainToBlacklist("gmail.com")
DisposableEmail.isDisposable("hello@gmail.com")   // true

DisposableEmail.removeDomainFromBlacklist("gmail.com")
DisposableEmail.isDisposable("gmail.com")         // true
```

## Docs

- [Architecture](docs/architecture.md)
