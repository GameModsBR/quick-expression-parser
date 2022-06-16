plugins {
    kotlin("multiplatform") version "1.6.21"
}

group = "br.com.gamemods.qep"
version = "1.0.0-SNAPSHOT"

repositories {
    mavenCentral()
}

java {
    targetCompatibility = JavaVersion.VERSION_1_8
    sourceCompatibility = targetCompatibility
}

kotlin {
    jvm {
        withJava()
        compilations.all {
            kotlinOptions {
                jvmTarget = "1.8"
            }
        }
    }
    targets.all {
        compilations.named("main") {
            kotlinOptions {
                allWarningsAsErrors = true
            }
        }
    }
    sourceSets {
        all {
            languageSettings.apply {
                optIn("kotlin.RequiresOptIn")
            }
        }
        commonMain {
            dependencies {
                implementation(kotlin("stdlib-jdk8"))
                implementation(kotlin("reflect"))
            }
        }
        commonTest {
            dependencies {
                api(kotlin("test"))
            }
        }
    }
}
