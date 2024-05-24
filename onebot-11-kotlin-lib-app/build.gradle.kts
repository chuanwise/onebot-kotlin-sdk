/*
 * Copyright 2024 Chuanwise and contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

plugins {
    kotlin("jvm") version "2.0.0-RC3"
}

repositories {
    mavenCentral()
}

dependencies {
    api(project(":onebot-kotlin-lib-app"))
    api(project(":onebot-11-kotlin-lib"))

    testApi(project(":onebot-kotlin-lib-app"))
    testApi(project(":onebot-11-kotlin-lib"))

    compileOnly(project(":onebot-kotlin-lib-ws"))
    compileOnly(project(":onebot-kotlin-lib-http"))
    testImplementation(project(":onebot-kotlin-lib-ws"))
    testImplementation(project(":onebot-kotlin-lib-http"))

    val log4jVersion: String by rootProject
    val kotlinLoggingVersion: String by rootProject
    val slf4jVersion: String by rootProject
    implementation("io.github.oshai:kotlin-logging-jvm:$kotlinLoggingVersion")

    testImplementation("org.slf4j:slf4j-api:$slf4jVersion")
    testImplementation("org.apache.logging.log4j:log4j-api:$log4jVersion")
    testImplementation("org.apache.logging.log4j:log4j-slf4j2-impl:$log4jVersion")
    testRuntimeOnly("org.apache.logging.log4j:log4j-core:$log4jVersion")

    val ktorVersion: String by rootProject
    implementation("io.ktor:ktor-server-core-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-websockets-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-netty-jvm:$ktorVersion")
    implementation("io.ktor:ktor-websockets:$ktorVersion")

    val junitVersion: String by rootProject
    testImplementation(platform("org.junit:junit-bom:$junitVersion"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}