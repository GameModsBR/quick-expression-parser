import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    kotlin("plugin.allopen") version "1.6.21"
    id("me.champeau.gradle.jmh") version "0.5.3"
    //id "com.vanniktech.dependency.graph.generator" version "0.5.0"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(parent!!)
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = sourceCompatibility
}

sourceSets.main {
    java.setSrcDirs(emptyList<Nothing>())
}

tasks.compileKotlin {
    kotlinOptions.allWarningsAsErrors = true
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

allOpen {
    annotations(
        "org.openjdk.jmh.annotations.BenchmarkMode",
        "org.openjdk.jmh.annotations.State"
    )
}

jmh {
    warmupIterations = 1
    warmup = "1s"

    iterations = 10
    timeOnIteration = "1s"
    
    fork = 3
    batchSize = 1

    timeUnit = "us"

    benchmarkMode = listOf("all")
}
