plugins {
    kotlin("jvm") version "1.9.10"
    kotlin("plugin.serialization") version "1.9.10"
}

group = "cn.chuanwise"
version = "0.1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    val ktorVersion = "2.3.10"
    implementation("io.ktor:ktor-client-websockets:${ktorVersion}")
//    implementation("io.ktor:ktor-client-cio:${ktorVersion}")
//    implementation("io.ktor:ktor-client-js:${ktorVersion}")
    implementation("io.ktor:ktor-client-okhttp:${ktorVersion}")

    val log4jVersion = "2.23.1"
    val kotlinLoggingVersion = "5.1.0"
    val slf4jVersion = "2.0.13"
    implementation("io.github.oshai:kotlin-logging-jvm:${kotlinLoggingVersion}")
    implementation("org.apache.logging.log4j:log4j-api:${log4jVersion}")
    runtimeOnly("org.apache.logging.log4j:log4j-core:${log4jVersion}")
    testRuntimeOnly("org.apache.logging.log4j:log4j-core:${log4jVersion}")

    implementation("org.slf4j:slf4j-api:${slf4jVersion}")
    implementation("org.apache.logging.log4j:log4j-slf4j2-impl:${log4jVersion}")

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:1.5.1")

    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}