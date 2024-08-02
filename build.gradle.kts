import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    kotlin("jvm") version "1.5.31"
    `maven-publish`
    id("com.github.johnrengelman.shadow") version "7.0.0"
}

group = "org.valarpirai"
version = "1.0.2"

repositories {
    mavenCentral()
}
java {
    sourceCompatibility = JavaVersion.VERSION_1_8
}

dependencies {
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("com.squareup.okhttp3:okhttp:4.10.0")
    implementation("commons-codec:commons-codec:1.17.1")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<ShadowJar>() {
    // Minimizing a shadow JAR
    minimize()
    relocate("com.google.gson", "org.valarpirai.shaded.com.google.gson")
    relocate("okhttp3", "org.valarpirai.shaded.okhttp3")
    relocate("okio", "org.valarpirai.shaded.okio")
    relocate("org.apache.commons", "org.valarpirai.shaded.org.apache.commons")
}

publishing {
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/valarpirai/disposable-emails")
            credentials {
                username = project.findProperty("gpr.user") as String? ?: System.getenv("USERNAME")
                password = project.findProperty("gpr.key") as String? ?: System.getenv("TOKEN")
            }
        }
    }
    publications {
        create<MavenPublication>("maven") {
            groupId = group as String
            artifactId = "disposable-email"
            version = version

            from(components["java"])
        }
    }
}
