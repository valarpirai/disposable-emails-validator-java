plugins {
    kotlin("jvm") version "1.5.31"
    `maven-publish`
}

group = "org.disposableemail"
version = "0.0.1"

repositories {
    mavenCentral()
}

dependencies {
    implementation("commons-codec:commons-codec:1.17.1")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
//kotlin {
//    jvmToolchain(11)
//}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = group as String
            artifactId = "org.disposable"
            version = version

            from(components["java"])
        }
    }
}
