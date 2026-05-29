# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Commands

```bash
# Build
./gradlew build

# Run tests
./gradlew test

# Run a single test class
./gradlew test --tests "org.disposableemail.DisposableEmailTest"

# Run a single test method
./gradlew test --tests "org.disposableemail.DisposableEmailTest.test_DisposableEmail"

# Build the shadow (fat) JAR for publishing
./gradlew shadowJar

# Publish to GitHub Packages (requires USERNAME and TOKEN env vars)
./gradlew publish
```

## Docs

- [README.md](README.md) — installation, usage examples, and public API reference
- [docs/architecture.md](docs/architecture.md) — package structure, bloom filter internals, DNS-over-HTTPS flow, publishing
