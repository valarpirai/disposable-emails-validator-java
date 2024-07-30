import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    kotlin("jvm") version "1.5.31"
    `maven-publish`
    id("com.github.johnrengelman.shadow") version "7.0.0"
}

group = "org.valarpirai"
version = "0.0.1"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("commons-codec:commons-codec:1.17.1")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
//kotlin {
//    jvmToolchain(11)
//}

tasks.withType<ShadowJar>() {
    relocate("com.google.gson", "com.valarpirai.shaded.com.google.gson")
    relocate("okhttp3", "com.valarpirai.shaded.okhttp3")
    relocate("org.apache.commons", "com.valarpirai.shaded.org.apache.commons")
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = group as String
            artifactId = "org.disposable-email"
            version = version

            from(components["java"])
        }
    }
}
