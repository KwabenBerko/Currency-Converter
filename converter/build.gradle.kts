@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.android.library)
    alias(libs.plugins.ktlint)
    alias(libs.plugins.sqldelight)
}

ktlint {
    filter {
        exclude { projectDir.toURI().relativize(it.file.toURI()).path.contains("/generated/") }
        exclude { projectDir.toURI().relativize(it.file.toURI()).path.contains("/kotlin/") }
    }
}

sqldelight {
    database("CurrencyConverterDatabase") {
        packageName = "com.kwabenaberko"
    }
}

kotlin {
    android()
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach {
        it.binaries.framework {
            baseName = "shared"
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(libs.coroutines.core)
                implementation(libs.sqldelight.coroutines.extensions)
                implementation(libs.multiplatformSettings)
                implementation(libs.multiplatformSettings.coroutines)
                implementation(libs.bundles.ktor.client)
                implementation(libs.ktor.serialization)
                implementation(libs.kotlinx.dateTime)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(projects.converterTest)
                implementation(kotlin("test"))
                implementation(libs.coroutines.test)
                implementation(libs.turbine)
                implementation(libs.kotest.assertions.core)
                implementation(libs.multiplatformSettings.test)
                implementation(libs.ktor.client.mock)
            }
        }
        val androidMain by getting {
            dependencies {
                implementation(libs.sqldelight.driver.android)
                implementation(libs.ktor.client.android)
                implementation(libs.icu4j)
            }
        }
        val androidTest by getting {
            dependencies {
                implementation(libs.sqldelight.driver.sqlite)
            }
        }
        val iosX64Main by getting
        val iosArm64Main by getting
        val iosSimulatorArm64Main by getting
        val iosMain by creating {
            dependsOn(commonMain)
            iosX64Main.dependsOn(this)
            iosArm64Main.dependsOn(this)
            iosSimulatorArm64Main.dependsOn(this)
            dependencies {
                implementation(libs.sqldelight.driver.native)
                implementation(libs.ktor.client.darwin)
            }
        }
        val iosX64Test by getting
        val iosArm64Test by getting
        val iosSimulatorArm64Test by getting
        val iosTest by creating {
            dependsOn(commonTest)
            iosX64Test.dependsOn(this)
            iosArm64Test.dependsOn(this)
            iosSimulatorArm64Test.dependsOn(this)
        }
    }
}

android {
    compileSdk = 33
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    defaultConfig {
        minSdk = 22
        targetSdk = 33
    }
    namespace = "com.kwabenaberko.currencyconverter"
}
