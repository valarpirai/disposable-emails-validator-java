plugins {
    kotlin("jvm") version "1.5.31"
    `maven-publish`
}

group = "org.disposableemail"
version = "0.0.1"

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    implementation("com.google.guava:guava:32.1.3-jre")
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
